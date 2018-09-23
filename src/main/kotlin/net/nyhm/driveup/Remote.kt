package net.nyhm.driveup

import java.io.File

/**
 * This class represents the files stored in the remote service and encapsulates the logic
 * to send content to the remote store. Use the [create] method to create a Remote instance.
 * Data is encrypted (by a given [Encryptor]) prior to being uploaded.
 */
class Remote private constructor(
    private val driver: GDriver,
    private val encryptor: Encryptor,
    private val parentId: String,
    private val remoteFiles: MutableSet<com.google.api.services.drive.model.File>
) {

  companion object {
    /**
     * Create a Remote instance.
     *
     * @param driver storage service
     * @param encryptor encryption service
     * @param uploadPath directory path in remote system to store files
     */
    fun create(driver: GDriver, encryptor: Encryptor, uploadPath: List<String>): Remote {
      val parentId = driver.createPath(uploadPath, "root").last()

      // TODO: Check for duplicates? Is model.File ok in Set (good equals)?
      val files = driver.remoteFiles(parentId).toMutableSet()

      return Remote(driver, encryptor, parentId, files)
    }

    fun mimeType(fileName: String): String {
      return if (fileName.endsWith(GPG_FILE_EXT)) GPG_MIME_TYPE
      else throw IllegalArgumentException("Can only upload gpg files")
    }

    fun File.mimeType() = mimeType(this.name)
  }

  fun fileCount() = remoteFiles.size

  fun totalBytes() = Bytes(remoteFiles.sumByLong { it.getSize() })

  fun hasFile(file: File) = encryptor.remoteName(file).let {
    name -> remoteFiles.any { it.name == name }
  }

  /**
   * Upload the file into the configured path, passing its contents through the configured [Encryptor].
   * The uploaded file name is given a GPG file extension (".gpg") and mime type.
   */
  fun upload(file: File) {
    val remoteName = encryptor.remoteName(file)
    val mimeType = mimeType(remoteName)
    val inputStream = encryptor.encrypt(file)
    remoteFiles.add(
        driver.upload(UploadSpecBuilder()
          .mimeType(mimeType)
          .name(remoteName)
          .parentId(parentId)
          .source(inputStream)
          .build()
    ))
  }
}