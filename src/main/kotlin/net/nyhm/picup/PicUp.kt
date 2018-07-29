package net.nyhm.picup

import com.github.ajalt.clikt.core.CliktCommand
import com.google.api.services.drive.DriveScopes
import net.nyhm.gdpush.GDriver
import java.io.File
import java.io.FileFilter
import java.nio.file.FileSystems
import java.nio.file.Files
import java.util.*

class PicUp {
  companion object {

    private const val UPLOAD_DIR = "PicsBackup"

    private val sourceDir = FileSystems.getDefault().getPath(
        System.getProperty("user.home"),
        "pics",
        "Pixel"
    )

    @JvmStatic
    fun main(args: Array<String>) = Cli().main(args)

    fun execute() {

      val localFiles = findLocalFiles()

      val driver = GDriver(
          "NEB Pics Backup",
          File("/client_secret.json"),
          File("credentials"),
          listOf(DriveScopes.DRIVE_FILE)
      )

      val dir = driver.findDirectory(UPLOAD_DIR)

      val dirId = dir?.id ?: driver.createDirectory(UPLOAD_DIR)
      println("Created dirId: $dirId")

      val remoteFiles = driver.remoteFiles(dirId).map { it.name }

      if (remoteFiles.isEmpty()) {
        println("No remote files")
      } else {
        //remoteFiles.forEach { println("> $it") }
      }

      localFiles
          .filter { !remoteFiles.contains(it.name + ".gpg") }
          .forEach { upload(it) }
    }

    private fun findLocalFiles(): MutableSet<File> {
      if (!Files.exists(sourceDir)) throw IllegalStateException("Source dir does not exist: $sourceDir")
      val files = sourceDir.toFile().listFiles(FileFilter {
        it.isFile && it.name.endsWith(".jpg", true)
      })
      return TreeSet(files.toList().take(5))
    }

    private fun upload(file: File) {
      val remoteName = file.name + ".gpg"
      println("^ $remoteName")
      // TODO: encrypt and upload
    }

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

  // TODO: Explicitly point to client_secret.json and credentials, rather than built-in

  override fun run() {
    /*
    if (auth) {
      TermUi.echo("Perform auth")
    }
    */

    PicUp.execute()
  }
}