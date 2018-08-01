package net.nyhm.picup

import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class Uploader(
    private val sourceDir: Path,
    private val remote: Remote
) {
  private val localFiles: MutableSet<File> by lazy {
    if (!Files.exists(sourceDir)) throw IllegalStateException("Source dir does not exist: $sourceDir")
    val files = sourceDir.toFile().listFiles(FileFilter {
      it.isFile && it.name.endsWith(".jpg", true)
    })
    TreeSet(files.toList())
  }

  fun localCount() = localFiles.size

  fun uploadCount() = localFiles.count { !remote.hasFile(it) }

  fun upload(limit: Int = 0) {
    var files = localFiles.filter { !remote.hasFile(it) }
    if (limit > 0) files = files.take(limit)
    println("Uploading ${files.size} file${if (files.size > 1) "s" else ""}...")
    files.forEach { remote.upload(it) }
  }
}