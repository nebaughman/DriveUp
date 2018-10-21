package net.nyhm.driveup

import com.google.api.services.drive.DriveScopes
import org.junit.Test
import java.io.File

/**
 * These are not unit tests. This represents a sample (test) app using GDriver to
 * interact with a Google Drive account.
 */
class TestGDriver {

  companion object {
    private const val UPLOAD_FILE_NAME = "test-upload-file.txt"
    private const val UPLOAD_FILE_PATH = "/$UPLOAD_FILE_NAME"
    private const val UPLOAD_FILE_TYPE = "text/plain"
    private val UPLOAD_PATH = listOf("test-root", "test-level-1", "test-level-2")
  }

  // TODO: Update this test; this no longer works as it did before...
  private val driver by lazy {
    GDriver(
        "TestApp",
        GDriver.readSecrets(File("credentials", "client_secret.json")),
        CredsStoreFactory(),
        listOf(DriveScopes.DRIVE_FILE) // TODO: only needed if establishing creds?
        //
        // Changing scopes, but using old StoredCredential file seems to use scopes granted to prior creds.
    )
  }

  @Test
  fun listRemoteFiles() = driver.remoteFiles().forEach { println(it) }

  @Test
  fun testFileUpload() {

    val parentId = driver.createPath(UPLOAD_PATH, "root").last()

    val file = driver.upload(UploadSpecBuilder()
        .mimeType(UPLOAD_FILE_TYPE)
        .name(UPLOAD_FILE_NAME)
        .source(TestGDriver::class.java.getResourceAsStream(UPLOAD_FILE_PATH))
        .parentId(parentId)
        .build()
    )
    println("Uploaded file: $file")

    driver.remoteFiles(parentId).forEach { println(it) }

    //println(driver.findDirectory(UPLOAD_PATH.first()))
    //println(driver.findDirectory(UPLOAD_PATH.first(), "root"))
  }
}