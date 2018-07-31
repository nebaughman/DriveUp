package net.nyhm.picup

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.google.api.services.drive.DriveScopes
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfig
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs
import net.nyhm.gdriver.GDriver
import net.nyhm.gdriver.UploadSpecBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.Streams
import java.io.*
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.security.Security
import java.util.*

private const val GPG_MIME_TYPE = "application/pgp-encrypted"

class PicUp(
    private val sourceDir: Path,
    private val remote: Remote
) {

  private val localFiles: MutableSet<File> by lazy {
    if (!Files.exists(sourceDir)) throw IllegalStateException("Source dir does not exist: $sourceDir")
    val files = sourceDir.toFile().listFiles(FileFilter {
      it.isFile && it.name.endsWith(".jpg", true)
    })
    TreeSet(files.toList())
  }

  fun localCount() = localFiles.size

  fun uploadCount() = localFiles.count { !remote.hasFile(it) }

  fun upload(limit: Int = 0) {
    var files = localFiles.filter { !remote.hasFile(it) }
    if (limit > 0) files = files.take(limit)
    println("Uploading ${files.size} file${if (files.size > 1) "s" else ""}...")
    files.forEach { remote.upload(it) }
  }

  companion object {
    @JvmStatic
    fun main(args: Array<String>) = Cli().main(args)
  }
}

class Remote private constructor(
    private val driver: GDriver,
    private val encryptor: Encryptor,
    private val parentId: String,
    private val remoteFiles: MutableSet<String>
) {

  companion object {
    fun create(driver: GDriver, encryptor: Encryptor, uploadRoot: String): Remote {
      val dir = driver.findDirectory(uploadRoot)
      val parentId = dir?.id ?: driver.createDirectory(uploadRoot)
      if (parentId.isBlank()) throw IllegalStateException("Could not obtain parent directory ID")
      //println("parentId: $parentId")

      // TODO: check for duplicates?
      val remoteFiles = driver.remoteFiles(parentId).map { it.name }.toMutableSet()

      return Remote(driver, encryptor, parentId, remoteFiles)
    }
  }

  fun remoteName(file: File) = file.name + ".gpg"

  fun fileCount() = remoteFiles.size

  fun hasFile(file: File) = remoteFiles.contains(remoteName(file))

  fun upload(file: File) {
    val remoteName = remoteName(file)
    val time = Stopwatch(file.name, file.length())
    driver.upload(UploadSpecBuilder()
        .mimeType(GPG_MIME_TYPE)
        .name(remoteName)
        .parentId(parentId)
        .source(encryptor.encrypt(file))
        .build()
    )
    remoteFiles.add(remoteName)
    println(time.report())
  }
}

class Encryptor(
    private val publicKey: Path,
    private val recipient: String
) {

  private val keyringConfig: KeyringConfig

  init {
    if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
      Security.addProvider(BouncyCastleProvider())
    }

    val publicKey = Files.readAllBytes(
        //Paths.get(Encryptor::class.java.getResource(PUB_KEY_FILE).toURI())
        publicKey
    )

    val keyring = KeyringConfigs.forGpgExportedKeys { _ -> null }
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
        .toRecipient(recipient)
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

    val remote = Remote.create(driver, encryptor, uploadRoot)

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

    val picup = PicUp(sourceDir, remote)

    println("Local files: ${picup.localCount()}")
    println("Remote files: ${remote.fileCount()}")
    println("Files to upload: ${picup.uploadCount()}")

    picup.upload(4)
  }
}

class Stopwatch(val name: String, val bytes: Long) {
  val start = System.currentTimeMillis()
  fun report(): String {
    val sec = (System.currentTimeMillis() - start) / 1000.0
    val mbps = (bytes * 8.0 / 1024 / 1024) / sec
    return "$name: ${bytes}b / ${format(sec)}s = ${format(mbps)} Mbps"
  }
  private fun format(num: Number) = "%.1f".format(num)
}