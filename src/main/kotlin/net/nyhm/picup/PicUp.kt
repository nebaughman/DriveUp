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

    val mb = uploader.localBytes / 1024 / 1024
    val up = uploader.uploadStats()

    println("Local files: ${uploader.localCount} (${mb} MB)")
    println("Remote files: ${remote.fileCount()}")
    println("Files to upload: ${up.count}")

    uploader.upload(2)
  }
}

class Stopwatch(val name: String, val bytes: Long) {
  val start = now()
  var end: Long? = null
  fun stop(): Long {
    if (end == null) end = now()
    return end!!
  }
  fun time() = (end ?: now()) - start
  fun now() =  System.currentTimeMillis()
  fun report(): String {
    val sec = time() / 1000.0
    val mbps = (bytes * 8.0 / 1024 / 1024) / sec
    return "$name: ${bytes}b / ${format(sec)}s = ${format(mbps)} Mbps"
  }
  private fun format(num: Number) = "%.1f".format(num)
}

// https://stackoverflow.com/a/37537228/6004010
inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
  var sum = 0L
  for (element in this) {
    sum += selector(element)
  }
  return sum
}