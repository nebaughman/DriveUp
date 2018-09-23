package net.nyhm.driveup

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfig
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.io.Streams
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
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

/**
 * This class encrypts files to a specified [recipient], given the recipient's GPG/PGP [publicKey].
 */
class GpgEncryptor(
    private val publicKey: Path,
    private val recipient: String
): Encryptor {

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

  override fun remoteName(source: File) = source.name + GPG_FILE_EXT

  /**
   * Encrypt the given [source] file.
   * This method produces an InputStream for consuming the encrypted content.
   */
  override fun encrypt(source: File): InputStream {
    // TODO: use PipedInputStream and PipedOutputStream
    //val dest = File("${source.name}.gpg")
    //val result = FileOutputStream(dest)
    val result = ByteArrayOutputStream()
    BouncyGPG.encryptToStream()
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