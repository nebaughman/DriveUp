package net.nyhm.driveup

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.Streams
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.security.Security

interface Encryptor {
  fun encrypt(source: File): InputStream
  fun remoteName(source: File): String
}

/**
 * An [Encryptor] that does not encrypt (identity encryptor)
 */
object NonEncryptor: Encryptor {
  override fun encrypt(source: File) = source.inputStream()
  override fun remoteName(source: File) = source.name
}

class GpgConfig private constructor(
    val publicKey: ByteArray,
    val recipient: String
) {

  init {
    initProvider()
  }

  internal val keyring = KeyringConfigs
      .forGpgExportedKeys { _ -> null }
      .also { it.addPublicKey(publicKey) }

  fun export(): ByteArray {
    val bytes = ByteArrayOutputStream()
    DataOutputStream(bytes).use { out ->
      out.writeInt(VERSION)
      out.writeInt(publicKey.size)
      out.write(publicKey)
      out.writeInt(recipient.length) // char count (not byte count)
      out.writeChars(recipient)
    }
    return bytes.toByteArray()
  }

  companion object {
    private const val VERSION = 1

    private fun initProvider() {
      if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
        Security.addProvider(BouncyCastleProvider())
      }
    }

    fun fromFile(publicKeyFile: Path, recipient: String): GpgConfig {
      val publicKey = Files.readAllBytes(publicKeyFile)
      return GpgConfig(publicKey, recipient)
    }

    fun fromBytes(bytes: ByteArray): GpgConfig {
      DataInputStream(ByteArrayInputStream(bytes)).use { data ->
        val ver = data.readInt()
        if (ver != VERSION) throw IllegalArgumentException("Unexpected version $ver, expected $VERSION")
        val publicKey = ByteArray(data.readInt())
        data.readFully(publicKey)
        val recipientLen = data.readInt() // char count (not byte count)
        var recipient = ""
        for (i in 0 until recipientLen) {
          recipient += data.readChar()
        }
        return GpgConfig(publicKey, recipient)
      }
    }
  }
}

/**
 * This class encrypts files to a specified [recipient], given the recipient's GPG/PGP [publicKey].
 */
class GpgEncryptor(private val config: GpgConfig): Encryptor {

  override fun remoteName(source: File) = source.name + GPG_FILE_EXT

  /**
   * Encrypt the given [source] file.
   * This method produces an InputStream for consuming the encrypted content.
   */
  override fun encrypt(source: File) = encrypt(source.inputStream())

  // TODO: implement this class as a Stream?
  //
  fun encrypt(input: InputStream): InputStream {
    // TODO: use PipedInputStream and PipedOutputStream
    //val dest = File("${source.name}.gpg")
    //val result = FileOutputStream(dest)
    val result = ByteArrayOutputStream()
    BouncyGPG.encryptToStream()
        .withConfig(config.keyring)
        .withStrongAlgorithms()
        .toRecipient(config.recipient)
        .andDoNotSign()
        .binaryOutput()
        .andWriteTo(result).use { output ->
          Streams.pipeAll(input, output)
        }
    return ByteArrayInputStream(result.toByteArray())
  }
}