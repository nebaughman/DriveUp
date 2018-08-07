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
    private val remoteFiles: MutableSet<String>
) {

  companion object {
    private const val GPG_MIME_TYPE = "application/pgp-encrypted"
    private const val GPG_FILE_EXT = ".gpg"

    /**
     * Create a Remote instance.
     *
     * @param driver storage service
     * @param encryptor encryption service
     * @param uploadPath directory path in remote system to store files
     */
    fun create(driver: GDriver, encryptor: Encryptor, uploadPath: List<String>): Remote {
      val parentId = driver.createPath(uploadPath, "root").last()

      // TODO: check for duplicates?
      val remoteFiles = driver.remoteFiles(parentId).map { it.name }.toMutableSet()

      return Remote(driver, encryptor, parentId, remoteFiles)
    }
  }

  fun remoteName(file: File) = file.name + GPG_FILE_EXT

  fun fileCount() = remoteFiles.size

  fun hasFile(file: File) = remoteFiles.contains(remoteName(file))

  /**
   * Upload the file into the configured path, passing its contents through the configured [Encryptor].
   * The uploaded file name is given a GPG file extension (".gpg") and mime type.
   */
  fun upload(file: File) {
    val remoteName = remoteName(file)
    driver.upload(UploadSpecBuilder()
        .mimeType(GPG_MIME_TYPE)
        .name(remoteName)
        .parentId(parentId)
        .source(encryptor.encrypt(file))
        .build()
    )
    remoteFiles.add(remoteName)
  }
}