package net.nyhm.picup

import com.github.ajalt.clikt.core.CliktCommand
import com.google.api.services.drive.DriveScopes
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfig
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs
import net.nyhm.gdpush.GDriver
import net.nyhm.gdpush.UploadSpecBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.Streams
import java.io.*
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.security.Security
import java.util.*

class PicUp {
  companion object {

    private const val UPLOAD_DIR = "PicsBackup"

    private const val GPG_MIME_TYPE = "application/pgp-encrypted"

    private val sourceDir = FileSystems.getDefault().getPath(
        System.getProperty("user.home"),
        "pics", "Pixel"
        //"tmp", "picup-test"
    )

    @JvmStatic
    fun main(args: Array<String>) = Cli().main(args)

    fun execute() {

      val encryptor = Encryptor // force initialization

      val driver = GDriver(
          "NEB Pics Backup",
          File("/client_secret.json"),
          File("credentials"),
          listOf(DriveScopes.DRIVE_FILE)
      )

      val dir = driver.findDirectory(UPLOAD_DIR)

      val parentId = dir?.id ?: driver.createDirectory(UPLOAD_DIR)
      if (parentId.isBlank()) throw IllegalStateException("Could not obtain parent directory ID")
      //println("parentId: $parentId")

      val remoteFiles = driver.remoteFiles(parentId).map { it.name }.also {
        val count = if (it.isEmpty()) "No" else it.size.toString()
        val noun = if (it.size == 1) "file" else "files"
        println("$count remote $noun")
      }

      findLocalFiles()
          .filter { !remoteFiles.contains(it.name + ".gpg") }
          .take(10) // for testing
          .also { println("${it.size} file${if (it.size > 1) "s" else ""} to upload...") }
          .forEach { upload(driver, encryptor, parentId, it) }
    }

    private fun findLocalFiles(): MutableSet<File> {
      if (!Files.exists(sourceDir)) throw IllegalStateException("Source dir does not exist: $sourceDir")
      val files = sourceDir.toFile().listFiles(FileFilter {
        it.isFile && it.name.endsWith(".jpg", true)
      })
      return TreeSet(files.toList())
    }

    private fun upload(driver: GDriver, encryptor: Encryptor, parentId: String, file: File) {
      val remoteName = file.name + ".gpg"
      println("^ ${file.name}(.gpg)")
      driver.upload(UploadSpecBuilder()
          .mimeType(GPG_MIME_TYPE)
          .name(remoteName)
          .parentId(parentId)
          .source(encryptor.encrypt(file))
          .build()
      )
    }
  }
}

object Encryptor {

  private const val PUB_KEY_FILE = "/public_key.asc"
  private const val RECIPIENT = "neb@nebaughman.com"

  private val keyringConfig: KeyringConfig

  init {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(BouncyCastleProvider())
    }

    val publicKey = Files.readAllBytes(
        Paths.get(Encryptor::class.java.getResource(PUB_KEY_FILE).toURI())
    )

    val keyring = KeyringConfigs.forGpgExportedKeys { keyID -> null }
    keyring.addPublicKey(publicKey)
    keyringConfig = keyring
  }

  fun encrypt(source: File): InputStream {
    // TODO: use PipedInputStream and PipedOutputStream
    //val dest = File("${source.name}.gpg")
    //val result = FileOutputStream(dest)
    val result = ByteArrayOutputStream()
    BouncyGPG
        .encryptToStream()
        .withConfig(keyringConfig)
        .withStrongAlgorithms()
        .toRecipient(RECIPIENT)
        .andDoNotSign()
        .binaryOutput()
        .andWriteTo(result).use { output ->
          Streams.pipeAll(source.inputStream(), output)
        }
    return ByteArrayInputStream(result.toByteArray())
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