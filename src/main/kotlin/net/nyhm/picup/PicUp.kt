package net.nyhm.picup

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
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

  val uploadRoot by option(
      "--upload-root",
      help = "Upload root directory name"
  ).default("PicsBackup")

  val appName by option(
      "--app-name",
      help = "Application name"
  ).default("NEB Pics Backup")

  val clientSecret by option(
      "--client-secret",
      help = "Application client secret json file"
  ).file(
      exists = true,
      fileOkay = true,
      folderOkay = false,
      writable = false,
      readable = true
  ).default(File("client_secret.json"))

  val credentialsPath by option(
      "--credentials-path",
      help = "Path to stored user credentials"
  ).file(
      exists = false,
      fileOkay = false,
      folderOkay = true,
      writable = true,
      readable = true
  ).default(File("credentials"))

  override fun run() {
    /*
    if (auth) {
      TermUi.echo("Perform auth")
    }
    */

    val encryptor = Encryptor(
        FileSystems.getDefault().getPath(
            System.getProperty("user.dir"), "public_key.asc"
        ),
        "neb@nebaughman.com"
    )

    val driver = GDriver(
        appName,
        clientSecret,
        credentialsPath,
        listOf(DriveScopes.DRIVE_FILE)
    )

    val uploadPath = listOf(uploadRoot, "Pixel")

    val remote = Remote.create(driver, encryptor, uploadPath)

    /*
    val files = remote.fileCount()
    val count = if (files == 0) "No" else files.toString()
    val noun = if (files == 1) "file" else "files"
    println("$count remote $noun")
    */

    val sourceDir = FileSystems.getDefault().getPath(
        System.getProperty("user.home"),
        "pics", "Pixel"
        //"tmp", "picup-test"
    )

    val uploader = Uploader.create(sourceDir, remote)

    val batch = uploader.createBatch(2)

    println("Local files: ${uploader.localCount} (${uploader.localBytes})")
    println("Remote files: ${remote.fileCount()}")
    println("Files to upload: ${batch.count} (${batch.bytes})")

    val stats = uploader.upload(batch)

    val remaining = uploader.createBatch()

    println("Remaining: ${remaining.count} (${remaining.bytes}) @ ${stats.mbps} = ${remaining.eta(stats.mbps)}")
  }
}