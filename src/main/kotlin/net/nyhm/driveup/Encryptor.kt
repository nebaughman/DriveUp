package net.nyhm.driveup

import com.google.protobuf.ByteString
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs
import net.nyhm.driveup.proto.GpgData
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.Streams
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
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

  fun export(): GpgData = GpgData.newBuilder()
      .setPublicKey(ByteString.copyFrom(publicKey))
      .setRecipient(recipient)
      .build()

  companion object {
    private fun initProvider() {
      if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
        Security.addProvider(BouncyCastleProvider())
      }
    }

    fun fromFile(publicKeyFile: Path, recipient: String) = GpgConfig(
        publicKeyFile.toFile().readBytes(),
        recipient
    )

    fun fromData(data: GpgData) = GpgConfig(
        data.publicKey.toByteArray(),
        data.recipient
    )
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