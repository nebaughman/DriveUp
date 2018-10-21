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
import com.google.api.client.util.store.MemoryDataStoreFactory
import com.google.api.services.drive.DriveScopes
import java.io.*
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream
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
      Init(), ListRemote(), Upload()
    ).main(args)

enum class Access(val scope: String) {
  FULL(DriveScopes.DRIVE),
  PATH(DriveScopes.DRIVE_FILE),
  READ(DriveScopes.DRIVE_READONLY)
}

const val CONFIG_VERSION = 1

class AppConfig(
    val appName: String,
    val clientSecret: ByteArray,
    val credsStore: ByteArray,
    val gpgConfig: GpgConfig
) {

  fun export(): ByteArray {
    val bytes = ByteArrayOutputStream()
    DataOutputStream(bytes).use { out ->
      out.writeInt(VERSION)
      out.writeInt(appName.length) // char count (not byte count)
      out.writeChars(appName)
      out.writeInt(clientSecret.size); println("clentSecret ${clientSecret.size}")
      out.write(clientSecret)
      out.writeInt(credsStore.size); println("credsStore ${credsStore.size}")
      out.write(credsStore)
      val gpg = gpgConfig.export()
      out.writeInt(gpg.size); println("gpg ${gpg.size}")
      out.write(gpg)
    }
    return bytes.toByteArray()
  }

  companion object {
    private const val VERSION = 1

    fun fromBytes(bytes: ByteArray) =
      DataInputStream(ByteArrayInputStream(bytes)).use {
        val ver = it.readInt()
        if (ver != VERSION) throw java.lang.IllegalArgumentException("Unexpected version $ver")

        var appName = ""
        val chars = it.readInt() // char count (not byte count)
        for (i in 0 until chars) appName += it.readChar()

        val clientSecret = ByteArray(it.readInt())
        it.readFully(clientSecret)
        println("clientSecret ${clientSecret.size}")

        val credsStore = ByteArray(it.readInt())
        it.readFully(credsStore)
        println("credsStore ${credsStore.size}")

        val gpg = ByteArray(it.readInt())
        it.readFully(gpg)
        println("gpg ${gpg.size}")

        AppConfig(appName, clientSecret, credsStore, GpgConfig.fromBytes(gpg))
      }
  }
}

class Init: CliktCommand(
    help = "Init DriveUp connection"
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
  ).default(Access.PATH)

  val output by option(
      "--output", "-o",
      help = "Save config to this file"
  ).file(
      exists = false,
      writable = true
  ).default(
      File("driveup.creds")
  )

  val overwrite by option(
      "--overwrite",
      help = "Overwrite any existing output file"
  ).flag(
      default = false
  )

  override fun run() {

    // .file(exists = false) means 'do not test for existence'
    if (output.exists() && !overwrite) {
      throw PrintMessage("Output file already exists: $output")
    }

    val gpgConfig = try {
      val gpgConfig = GpgConfig.fromFile(
          publicKey.toPath(),
          encryptionRecipient
      )
      GpgEncryptor(gpgConfig).encrypt("Test source".byteInputStream())
      GpgConfig.fromBytes(gpgConfig.export()) // sanity check that export is functional
    } catch (e: Exception) {
      throw PrintMessage("Encryption config failed") // TODO: be more helpful
    }

    val credsStore = try {

      val driver = GDriver(
          appName,
          clientSecret.readBytes(),
          CredsStoreFactory,
          listOf(access.scope)
      )

      driver.remoteDirs()
      //
      // TODO: just call something for quicker info (metadata?)
      // Or, be more comprehensive (create & delete a test path & file), depending on Access level

      val bytes = CredsStoreFactory.export()
      CredsStoreFactory.restore(bytes) // sanity check that export is functional
      bytes

    } catch (e: IOException) {
      var error = e.message ?: "Error accessing Drive"
      if (error.contains("access_denied")) error = "Access denied to Drive"
      throw PrintMessage(error)
    }

    val config = AppConfig(appName, clientSecret.readBytes(), credsStore, gpgConfig).export()
    println("write bytes ${config.size}")
    AppConfig.fromBytes(config) // sanity check

    DataOutputStream(BufferedOutputStream(GZIPOutputStream(FileOutputStream(output)))).use { out ->
      out.writeInt(CONFIG_VERSION)
      out.writeInt(config.size)
      out.write(config)
    }
  }
}

/**
 * Main command-line executable (application entry point)
 */
class DriveUp: CliktCommand(
    help = "Send files to Google Drive"
) {

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
    // init creates config
    if (context.invokedSubcommand !is Init) {
      try {
        // provide AppConfig to subcommands
        val bytes = DataInputStream(GZIPInputStream(FileInputStream(configFile))).use {
          val ver = it.readInt()
          if (ver != CONFIG_VERSION) throw PrintMessage("Invalid config version $ver")
          val bytes = ByteArray(it.readInt())
          it.readFully(bytes)
          bytes
        }
        println("read bytes ${bytes.size}")
        context.obj = AppConfig.fromBytes(bytes)
      } catch (e: Exception) {
        e.printStackTrace()
        var msg = "Unable to load $configFile"
        if (e.message != null) msg += ": " + e.message
        throw PrintMessage(msg)
      }
    }
  }
}

class ListRemote: CliktCommand(
    name = "list",
    help = "List remote files (that are accessible to credentials)"
) {

  private val config: AppConfig by requireObject()

  override fun run() {

    CredsStoreFactory.restore(config.credsStore)

    val driver = GDriver(
        config.appName,
        config.clientSecret,
        CredsStoreFactory,
        listOf(DriveScopes.DRIVE_READONLY)
    )

    driver.remoteFiles().forEach {
      echo(it.name)
    }
  }
}

class Upload: CliktCommand(
    help = "Encrypt and upload files"
) {
  // TODO: Encryption optional
  /*
  val noEncryption by option(
      "--no-encryption",
      help = "Do not use encryption"
  ).flag(default = false)
  */

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

  val config: AppConfig by requireObject()

  override fun run() {

    /*
    val encryptor = if (noEncryption) {
      NonEncryptor
    } else if (encryptionRecipient == null) {
      throw MissingParameter("Encryption recipient required")
    } else {
      GpgEncryptor(
          publicKey.toPath(),
          encryptionRecipient!!
      )
    }
    */
    /*
    val encryptor = GpgEncryptor(
        publicKey.toPath(),
        encryptionRecipient
    )
    */
    val encryptor = NonEncryptor
    val remoteRoot = "TEST"

    CredsStoreFactory.restore(config.credsStore)

    val driver = GDriver(
        config.appName,
        config.clientSecret,
        CredsStoreFactory,
        listOf(DriveScopes.DRIVE_FILE)
    )

    val uploadPath = listOf(remoteRoot, localChild)

    val remote = Remote.create(driver, encryptor, uploadPath)

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
class Report(
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