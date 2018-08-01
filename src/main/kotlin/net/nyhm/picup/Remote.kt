package net.nyhm.picup

import net.nyhm.gdriver.GDriver
import net.nyhm.gdriver.UploadSpecBuilder
import java.io.File

class Remote private constructor(
    private val driver: GDriver,
    private val encryptor: Encryptor,
    private val parentId: String,
    private val remoteFiles: MutableSet<String>
) {

  companion object {
    private const val GPG_MIME_TYPE = "application/pgp-encrypted"

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