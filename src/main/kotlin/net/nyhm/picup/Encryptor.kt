package net.nyhm.picup

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