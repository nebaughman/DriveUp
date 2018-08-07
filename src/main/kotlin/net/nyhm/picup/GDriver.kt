package net.nyhm.picup

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.AbstractInputStreamContent
import com.google.api.client.http.FileContent
import com.google.api.client.http.InputStreamContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

const val DIR_MIME_TYPE = "application/vnd.google-apps.folder"

/**
 * This class encapsulates interaction with Google Drive API.
 *
 * Scopes are tied to stored credentials. Cannot re-use stored credentials if changing scopes.
 */
class GDriver(
    val applicationName: String,
    val clientSecretFile: java.io.File,
    val credentialsDirectory: java.io.File,
    val scopes: List<String>
) {
  private val jsonFactory: JacksonFactory = JacksonFactory.getDefaultInstance()

  /*
   * Global instance of the scopes required by this quickstart.
   * If modifying these scopes, delete your previously saved credentials/ folder.
   */
  //private val scopes = listOf(DriveScopes.DRIVE_METADATA_READONLY)

  private val service by lazy {
    val httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    Drive.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
        .setApplicationName(applicationName)
        .build()
  }

  /**
   * Creates an authorized Credential object.
   * @param httpTransport The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If there is no client_secret.
   */
  private fun getCredentials(httpTransport: NetHttpTransport): Credential {
    // Load client secrets.
    val input = GDriver::class.java.getResourceAsStream(clientSecretFile.absolutePath)
        ?: clientSecretFile.inputStream()
    val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(input))

    // Build flow and trigger user authorization request.
    val flow = GoogleAuthorizationCodeFlow.Builder(
        httpTransport, jsonFactory, clientSecrets, scopes)
        .setDataStoreFactory(FileDataStoreFactory(credentialsDirectory))
        .setAccessType("offline")
        .build()

    return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
  }

  fun printAllObjects() {
    print("Retrieving objects...")
    val files = remoteObjects()
    println("${files.size} files")
    files.forEach { println(it) }
  }

  /**
   * Find the directory with the given name.
   * Optionally, directory must have specified parentId.
   * Use 'root' as the parentId to search only the drive root.
   * Returns null if no such directory.
   * Throws exception if more than one directory has this name.
   */
  fun findDirectory(name: String, parentId: String? = null): File? {
    val query = GdQueryBuilder().isDir().and("name", name)
    if (parentId != null) query.withParent(parentId)
    val dirs = remoteSearch(query)
    if (dirs.size > 1) throw IllegalStateException("More than one directory of given name: $name")
    return dirs.firstOrNull()
  }

  fun remoteDirs() = remoteSearch(GdQueryBuilder().isDir())

  fun remoteFiles(parent: File) = remoteFiles(parent.id)

  fun remoteFiles(parentId: String) = remoteSearch(
      GdQueryBuilder().isFile().withParent(parentId) //.append("'${parentId}' in parents")
  )

  fun remoteFiles() = remoteSearch(GdQueryBuilder().isFile())

  fun remoteObjects() = remoteSearch(GdQueryBuilder())

  fun remoteSearch(query: GdQueryBuilder): List<File> {
    val files = mutableListOf<File>()
    var pageToken: String? = null
    do {
      val request = service.files().list()
          //.setPageSize(10)
          .setPageToken(pageToken)
          //.setFields("nextPageToken, files(id, name, size)")
          .setQ(query.build())
      val result = request.execute()
      val batch = result.files
      if (batch != null) files.addAll(batch)
      pageToken = result.nextPageToken
    } while (pageToken != null)
    return files
  }

  fun upload(spec: UploadSpec): String {
    val fileMetadata = File()
    fileMetadata.name = spec.name
    fileMetadata.parents = spec.parents
    val remote = service.files().create(fileMetadata, spec.content)
        .setFields("id") // could add "parents" if parent specified
        .execute()
    return remote.id
  }

  fun createDirectory(name: String, parentId: String? = null): String {
    val fileMetadata = File()
    fileMetadata.name = name
    fileMetadata.mimeType = DIR_MIME_TYPE
    if (parentId != null) fileMetadata.parents = listOf(parentId)
    val dir = service.files().create(fileMetadata)
        .setFields("id")
        .execute()
    return dir.id
  }

  /**
   * Create the path of directory names, if it does not yet exist.
   *
   * If [rootId] is given, then the path starts within that directory.
   * Use 'root' as the rootId to start in the drive root.
   * Otherwise, the first path element is located anywhere, and will
   * throw an exception if not unique (see [findDirectory]).
   *
   * Elements of the path that do not exist are created.
   *
   * The list of path IDs associated with the given path names is returned
   * (which does not include the rootId).
   */
  fun createPath(path: List<String>, rootId: String? = null): List<String> {
    val ids = mutableListOf<String>()
    var parentId = rootId
    path.forEach {
      val dir = findDirectory(it, parentId)
      val dirId = dir?.id ?: createDirectory(it, parentId)
      ids.add(dirId)
      parentId = dirId
    }
    return ids
  }
}

data class UploadSpec(
    val name: String,
    val content: AbstractInputStreamContent,
    val parentId: String? = null
) {
  val parents = if (parentId != null) listOf(parentId) else null
}

/**
 * [UploadSpec] builder.
 *
 * Rules:
 * - source file or stream (not both) required
 * - mimeType required
 * - name required, must be non-empty
 *   - if name is not set, and file source given, file.name is used
 *   - if name and file are provided, given name is used
 */
class UploadSpecBuilder {

  private var name: String? = null
  private var mimeType: String? = null
  private var file: java.io.File? = null
  private var stream: InputStream? = null
  private var parentId: String? = null

  fun source(file: java.io.File) = apply {
    this.file = file
  }

  fun source(stream: InputStream) = apply {
    this.stream = stream
  }

  fun mimeType(mimeType: String) = apply {
    this.mimeType = mimeType
  }

  fun name(name: String) = apply {
    this.name = name
  }

  fun parentId(parentId: String) = apply {
    this.parentId = parentId
  }

  fun build(): UploadSpec {
    testValidity()
    val source = if (file != null) {
      FileContent(mimeType, file)
    } else {
      InputStreamContent(mimeType, stream)
    }
    return UploadSpec(
        name ?: file?.name ?: fail("Unable to determine name"),
        source,
        parentId
    )
  }

  fun valid(): Boolean {
    return try {
      testValidity()
      true
    } catch (e: Exception) {
      false
    }
  }

  fun testValidity() {
    if (file == null && stream == null) fail("Must specify file or stream source")
    if (file != null && stream != null) fail("Provide either file or stream source (not both)")
    if (name == null && file == null) fail("Must provide name (or file)")
    if (name != null && name.isNullOrEmpty()) fail("Name must not be empty")
    if (mimeType == null) fail("Must specify mime type")
  }

  private fun fail(msg: String): Nothing = throw throw IllegalArgumentException(msg)
}

// TODO: It really seems like this should be part of the Google Drive API...
// Do more research to see if some such thing exists.
//
/**
 * The Google Drive API uses a builder pattern to construct API requests, but a
 * major portion of that is a complex query, which is simply provided as a string.
 * This build attempts to improve the reliability of building these query strings.
 *
 * This is far from complete and must be used with care. If built incorrectly,
 * this will result in invalid query strings.
 */
class GdQueryBuilder {

  private var query: String = ""

  /**
   * Not trashed by default; must ask for trashed state if desired
   */
  constructor(trashed: Boolean = false) { isTrashed(trashed) }

  constructor(field: String, value: String): this() { and(field, value) }

  constructor(field: String, value: Boolean): this() { and(field, value) }

  fun append(clause: String, conjunction: String = "and") = apply {
    if (query.isNotEmpty()) query += " $conjunction "
    query += clause
  }

  fun withParent(parentId: String) = apply {
    append("'${parentId}' in parents")
  }

  fun and(field: String, value: String) = append("$field = '$value'")

  fun and(field: String, value: Boolean) = apply {
    if (query.isNotEmpty()) query += " and "
    query += "$field = ${value.toString().toLowerCase()}"
  }

  fun isDir() = apply { and("mimeType", DIR_MIME_TYPE) }

  fun isFile() = apply { append("mimeType != '${DIR_MIME_TYPE}'")}

  fun isTrashed(trashed: Boolean) = apply { and("trashed", trashed) }

  fun notTrashed() = apply { isTrashed(false) }

  fun build() = if (query.isEmpty()) null else query
}