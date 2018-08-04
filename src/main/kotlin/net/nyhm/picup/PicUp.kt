package net.nyhm.picup

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.google.api.services.drive.DriveScopes
import net.nyhm.gdriver.GDriver
import java.io.File
import java.nio.file.FileSystems

class PicUp {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) = Cli().main(args)
  }
}

class Cli: CliktCommand() {

  // TODO: Explicit auth step (not just side-effect of not finding credentials)
  /*
  val auth by option(
      "--auth", "--authenticate",
      help="Perform OAuth authentication"
  ).flag(default = false)
  */

  val appName by option(
      "--app-name",
      help = "Application name (arbitrary)"
  ).default(
      "NEB Pics Backup"
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
  ).default(
      File("client_secret.json")
  )

  val credentialsPath by option(
      "--credentials-path",
      help = "Path to stored user credentials"
  ).file(
      exists = false,
      fileOkay = false,
      folderOkay = true,
      writable = true,
      readable = true
  ).default(
      File("credentials")
  )

  val publicKey by option(
      "--public-key",
      help = "GPG/PGP public key file"
  ).file(
      exists = true,
      fileOkay = true,
      folderOkay = false,
      writable = false,
      readable = true
  ).default(
      File("public_key.asc")
  )

  val encryptionRecipient by option(
      "--encryption-recipient",
      help = "GPG/PGP recipient identifier (eg, email address)"
  ).required() // TODO: only if not justCheck

  val remoteRoot by option(
      "--remote-root",
      help = "Remote upload root directory"
  ).default(
      "PicsBackup"
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
      help = "Report local and remote files (no uploading)"
  ).flag(default = false)

  override fun run() {
    /*
    if (auth) {
      TermUi.echo("Perform auth")
    }
    */

    val encryptor = Encryptor(
        publicKey.toPath(),
        encryptionRecipient
    )

    val driver = GDriver(
        appName,
        clientSecret,
        credentialsPath,
        listOf(DriveScopes.DRIVE_FILE)
    )

    val uploadPath = listOf(remoteRoot, localChild)

    val remote = Remote.create(driver, encryptor, uploadPath)

    /*
    val files = remote.fileCount()
    val count = if (files == 0) "No" else files.toString()
    val noun = if (files == 1) "file" else "files"
    println("$count remote $noun")
    */

    val sourceDir = File(localRoot, localChild).toPath()

    val uploader = Uploader.create(sourceDir, remote)
    val remaining = uploader.createBatch() // no limit
    val batch = uploader.createBatch(uploadLimit)

    println("Local files: ${uploader.localCount} (${uploader.localBytes})")
    println("Remote files: ${remote.fileCount()}")
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