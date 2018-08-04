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

  fun upload(batch: UploadBatch, listener: (UploadStat) -> Unit = {}): UploadStats {
    val stats = mutableListOf<UploadStat>()
    batch.files.forEach {
      val start = now()
      remote.upload(it)
      val stat = UploadStat(it, now() - start)
      stats.add(stat)
      listener.invoke(stat)
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
  val bytes = Bytes(file.length())
  val time = Time(millis)
  val mbps = Mbps(bytes, time)
}

class UploadStats(val stats: List<UploadStat>) {
  val count = stats.size
  val bytes = Bytes(stats.sumByLong { it.bytes.bytes })
  val millis = stats.sumByLong { it.millis }
  val time = Time(millis)
  val mbps = Mbps(bytes, time)
}

class RateMonitor(val historyCount: Int = 0) {
  private val stats = mutableListOf<UploadStat>()
  fun add(stat: UploadStat) {
    stats.add(stat)
    if (historyCount > 0) while (stats.size > historyCount) stats.removeAt(0)
  }
  fun stats() = UploadStats(stats)
}