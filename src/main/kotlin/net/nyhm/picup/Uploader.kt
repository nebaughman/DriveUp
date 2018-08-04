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

  val localBytes by lazy { Bytes(localFiles.sumByLong { it.length() }) }

  val localCount = localFiles.size

  fun createBatch(limit: Int = 0): UploadBatch {
    var files = localFiles.filter { !remote.hasFile(it) }
    if (limit > 0) files = files.take(limit)
    return UploadBatch(files)
  }

  fun upload(batch: UploadBatch): UploadStats { // TODO: progress listener
    val stats = mutableListOf<UploadStat>()
    batch.files.forEach {
      val start = now()
      remote.upload(it)
      stats.add(UploadStat(it, now() - start))
    }
    return UploadStats(stats)
  }

  private fun now() = System.currentTimeMillis()
}

data class UploadBatch(
    val files: List<File>
) {
  val count = files.size
  val bytes = Bytes(files.sumByLong { it.length() })
  fun eta(rate: Mbps) = bytes.eta(rate)
}

data class UploadStat(
    val file: File,
    val millis: Long
) {
  val bytes = file.length()
  val mbps = Mbps(bytes, millis)
}

class UploadStats(val stats: List<UploadStat>) {
  val bytes = stats.sumByLong { it.bytes }
  val millis = stats.sumByLong { it.millis }
  val mbps = Mbps(bytes, millis)
}
