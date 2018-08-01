package net.nyhm.picup

import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Uploader private constructor(
    private val localFiles: Set<File>,
    private val remote: Remote
) {

  companion object {
    fun create(sourceDir: Path, remote: Remote): Uploader {
      if (!Files.exists(sourceDir)) throw IllegalArgumentException("Source dir does not exist: $sourceDir")
      val files = sourceDir.toFile().listFiles(FileFilter {
        it.isFile && it.name.endsWith(".jpg", true)
      })
      val localFiles = TreeSet(files.toList())
      return Uploader(localFiles, remote)
    }
  }

  val localBytes by lazy { localFiles.sumByLong { it.length() } }

  val localCount = localFiles.size

  fun uploadStats(): UploadStats {
    var count = 0
    var bytes = 0L
    localFiles
        .filter { !remote.hasFile(it) }
        .also { count = it.size }
        .forEach { bytes += it.length() }
    return UploadStats(count, bytes)
  }

  fun upload(limit: Int = 0) {
    var files = localFiles.filter { !remote.hasFile(it) }
    if (limit > 0) files = files.take(limit)
    println("Uploading ${files.size} file${if (files.size > 1) "s" else ""}...")
    files.forEach { remote.upload(it) }
  }
}

data class UploadStats(
    val count: Int,
    val bytes: Long
)