package net.nyhm.driveup

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.PrintMessage
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.google.api.services.drive.DriveScopes
import com.google.protobuf.ByteString
import com.google.protobuf.util.JsonFormat
import net.nyhm.driveup.proto.Access
import net.nyhm.driveup.proto.AppConfig
import java.io.*
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Main entry point
 */
fun main(args: Array<String>) = DriveUp()
    .versionOption(
      version = Version.version,
      help = "Show the version and exit",
      message = { "DriveUp v$it" }
    ).subcommands(
      Init(), Json(), ListRemote(), Upload()
    ).main(args)

/**
 * Map of [Access] values to [DriveScopes]
 */
val Scopes = mapOf(
  Access.READ to DriveScopes.DRIVE_READONLY,
  Access.PATH to DriveScopes.DRIVE_FILE,
  Access.FULL to DriveScopes.DRIVE
)

const val CONFIG_VERSION = 1

class Init: CliktCommand(
    help = "Init DriveUp credentials"
) {

  val appName by option(
      "--app-name",
      help = "Application name (arbitrary)"
  ).default(
      "DriveUp"
  )

  val clientSecret by option(
      "--client-secret",
      help = "Application client secret json file"
  ).file(
      exists = true,
      fileOkay = true,
      folderOkay = false,
      writable = false,
      readable = true
  ).required()
  /*.default(
      File("client_secret.json")
  )*/

  val publicKey by option(
      "--public-key",
      help = "GPG/PGP public key file"
  ).file(
      exists = true,
      fileOkay = true,
      folderOkay = false,
      writable = false,
      readable = true
  ).required() // TODO: allow non-encrypted config

  val encryptionRecipient by option(
      "--encryption-recipient",
      help = "GPG/PGP recipient identifier (eg, email address)"
  ).required() // TODO: allow non-encrypted config

  val access: Access by option(
      "--access",
      help = "Drive access level"
  ).choice(
      Access.values().associateBy { it.name.toLowerCase() }
  ).required() // .default(Access.PATH)

  val output by option(
      "--output",
      help = "Save config to this file"
  ).file(
      exists = false,
      writable = true,
      folderOkay = false,
      fileOkay = true
  ).default(
      File("driveup.creds")
  )

  val overwrite by option(
      "--overwrite",
      help = "Overwrite existing output file"
  ).flag(
      default = false
  )

  override fun run() {

    // .file(exists = false) means 'do not test for existence'
    if (output.exists() && !overwrite) {
      throw PrintMessage("Output file already exists: $output")
    }

    val gpgData = try {
      val gpgConfig = GpgConfig.fromFile(
          publicKey.toPath(),
          encryptionRecipient
      )
      GpgEncryptor(gpgConfig).encrypt("Test source".byteInputStream()) // test encryption
      val data = gpgConfig.export()
      GpgConfig.fromData(data) // sanity check save/load
      data
    } catch (e: Exception) {
      throw PrintMessage("Encryption config failed") // TODO: be more helpful
    }

    val credsStore = try {

      val credsStore = CredsStoreFactory() // empty creds store
      val secrets = GDriver.readSecrets(clientSecret.readText())

      val driver = GDriver(
          appName,
          secrets,
          credsStore,
          listOf(Scopes[access]!!)
      )

      driver.remoteDirs()
      //
      // TODO: just call something for quicker info (metadata?)
      // Or, be more comprehensive (create & delete a test path & file), depending on Access level

      val data = credsStore.export()
      CredsStoreFactory(data) // sanity check save/load
      data
    } catch (e: IOException) {
      var error = e.message ?: "Error accessing Drive"
      if (error.contains("access_denied")) error = "Access denied to Drive"
      throw PrintMessage(error)
    }

    val config = AppConfig.newBuilder()
        .setVersion(CONFIG_VERSION)
        .setAppName(appName)
        .setAccess(access)
        .setClientSecrets(ByteString.copyFrom(clientSecret.readBytes()))
        .putAllCredsStore(credsStore)
        .setGpgData(gpgData)
        .build()

    BufferedOutputStream(GZIPOutputStream(FileOutputStream(output))).use { out ->
        config.writeTo(out)
    }
  }
}

class Json: CliktCommand(
    help = "Print a config file as JSON"
) {

  private val config: AppConfig by requireObject()

  override fun run() {
    echo(JsonFormat.printer().print(config))
  }
}

/**
 * Parent command, which loads saved config file, unless subcommand is `init`,
 * which creates the config file.
 */
class DriveUp: CliktCommand(
    help = "Send files to Google Drive"
) {

  // TODO: Instead of Init --output option, pass this to Init as the output target
  val configFile by option(
      "--config",
      help = "Path to credentials file (created with init)"
  ).file(
      exists = true,
      fileOkay = true,
      folderOkay = false,
      readable = true
  ).default(
      File("driveup.creds")
  )

  override fun run() {
    // init creates config (other commands need it)
    if (context.invokedSubcommand !is Init) {
      // provide Config to subcommands
      context.obj = AppConfig
          .newBuilder()
          .mergeFrom(GZIPInputStream(FileInputStream(configFile)))
          .build()
    }
  }
}

class ListRemote: CliktCommand(
    name = "list",
    help = "List remote files (that are accessible to credentials)"
) {

  private val config by requireObject<AppConfig>()

  // TODO: api v3 has no maxResultSize;
  // limit manually by setting reasonable page size, iterate until limit reached;
  // however, limit is not very useful without order by and offset
  /*
  val limit by option(
      "--limit",
      help = "Maximum number of results"
  ).int()
  */

  override fun run() {

    val driver = GDriver(
        config.appName,
        GDriver.readSecrets(config.clientSecrets.toByteArray()),
        CredsStoreFactory(config.credsStoreMap),
        listOf(Scopes[config.access]!!)
    )

    // search for _all_ dirs (brute force)
    val dirs = driver
        .remoteSearch(GdQueryBuilder().isDir())
        .associate { it.id to it.name }

    // search for all files
    driver.remoteFiles().map {
      val parent = dirs[it.parents[0]] // TODO: More robust & build full path
      arrayOf(parent, it.name).joinToString("/", ".../")
    }.sorted().forEach { echo(it) }
  }
}

class Upload: CliktCommand(
    help = "Encrypt and upload files"
) {
  val noEncryption by option(
      "--no-encryption",
      help = "Do not use encryption"
  ).flag(default = false)

  val remoteRoot by option(
      "--remote-root",
      help = "Remote upload root directory"
  ).default(
      "DriveUp"
  )

  val uploadLimit by option(
      "--upload-limit",
      help = "Max files to upload (0 for no limit)"
  ).int().default(0).validate {
    require(it >= 0) { "Limit must be >= 0" }
  }

  val localRoot by option(
      "--local-root",
      help = "Local source root directory"
  ).file(
      exists = true,
      fileOkay = false,
      folderOkay = true,
      writable = false,
      readable = true
  ).required()

  val localChild by option(
      "--local-child",
      help = "Child directory under local root to upload into remote root"
  ).required().validate {
    val file = File(localRoot, it)
    require(file.exists() && file.isDirectory) { "Invalid child directory" }
  }

  val justCheck by option(
      "--just-check",
      help = "Report local and remote files (no uploading, see docs for caveats)"
  ).flag(default = false)

  val fileExtensions by option(
      "--file-extensions",
      help = "Include files with these extensions " +
             "(as comma-delimited list, or this option may be given multiple times)"
  ).multiple()

  // TODO: recursive
  /*
  val recursive by option(
      "--recursive", "-r",
      help = "Upload into subdirectories"
  ).flag(default = false)
  */

  private val config: AppConfig by requireObject()

  override fun run() {

    val driver = GDriver(
        config.appName,
        GDriver.readSecrets(config.clientSecrets.toByteArray()),
        CredsStoreFactory(config.credsStoreMap),
        listOf(DriveScopes.DRIVE_FILE)
    )

    val encryptor = if (noEncryption) {
      NonEncryptor
    } else {
      GpgEncryptor(GpgConfig.fromData(config.gpgData))
    }

    val uploadPath = listOf(remoteRoot, localChild)

    val remote = Remote.create(
        driver,
        encryptor,
        uploadPath
    )

    val sourceDir = File(localRoot, localChild).toPath()

    // build set of acceptable file extensions
    val ext = fileExtensions.flatMap { it.split(",") }.toSet()

    // filter to specified extensions (or any file if none given)
    val filter = FileFilter { ext.isEmpty() || ext.contains(it.extension) }

    val uploader = Uploader.create(sourceDir, remote, filter)
    val remaining = uploader.createBatch() // no limit
    val batch = uploader.createBatch(uploadLimit)

    println("Local path: $sourceDir")
    println("Local files: ${uploader.localCount} (${uploader.localBytes})")
    println("Remote path: /${uploadPath.joinToString("/")}")
    println("Remote files: ${remote.fileCount()} (${remote.totalBytes()})")
    println("Total remaining: ${remaining.count} (${remaining.bytes})")
    println("Files to upload: ${batch.count} (${batch.bytes})")

    if (justCheck) { // do not upload
      println("Exiting dry run")
      return
    }

    val report = Report(batch) { uploader.createBatch() }
    val stats = uploader.upload(batch) { println(report.add(it)) }

    println("Uploaded ${stats.count} (${stats.bytes}) @ ${stats.mbps} = ${stats.time} | Remaining: ${Report.batchReport(uploader.createBatch(), stats.mbps)}")
  }
}

/**
 * Helper to report ongoing upload stats
 */
private class Report(
    val batch: UploadBatch,
    val totalRemaining: () -> UploadBatch
) {
  val monitor = RateMonitor(10)
  val uploaded = mutableSetOf<File>()
  fun add(stat: UploadStat): String {
    monitor.add(stat)
    uploaded.add(stat.file)
    val batchRemaining = UploadBatch(batch.files.minus(uploaded))
    val rate = monitor.stats().mbps
    var report = "${stat.file.name}: ${stat.bytes} / ${stat.time} = ${stat.mbps}"
    report += " | "
    report += "Batch remaining: ${batchReport(batchRemaining, rate)}"
    report += " | "
    report += "Total remaining: ${batchReport(totalRemaining.invoke(), rate)}"
    return report
  }
  companion object {
    fun batchReport(batch: UploadBatch, rate: Mbps) =
        "${batch.count} (${batch.bytes}) @ $rate = ${batch.eta(rate)}"
  }
}