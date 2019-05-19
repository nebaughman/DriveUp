package net.nyhm.driveup

import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

/**
 * This class encapsulates the file upload logic.
 */
class Uploader private constructor(
    private val localFiles: Set<File>,
    private val remote: Remote
) {
  companion object {
    fun create(
        source: Path,
        remote: Remote,
        fileFilter: FileFilter = FileFilter { true }
    ): Uploader {
      if (!Files.exists(source)) {
        throw IllegalArgumentException("Source does not exist: $source")
      }
      // source may be a single file or a directory
      val sourceFile = source.toFile()
      val files: Array<File> = if (Files.isRegularFile(source)) {
        if (fileFilter.accept(sourceFile)) arrayOf(sourceFile)
        else emptyArray()
      } else {
        sourceFile.listFiles(FileFilter {
          it.isFile && fileFilter.accept(it)
        })
      }
      val localFiles = TreeSet(files.toList()).toSet()
      return Uploader(localFiles, remote)
    }
  }

  val localBytes by lazy { Bytes(localFiles.sumByLong { it.length() }) }

  val localCount = localFiles.size

  /**
   * Prepare an [UploadBatch] to [upload].
   *
   * The batch will only select files that are not currently in the remote file set.
   * Only create and upload one batch at a time (else the same files may be selected
   * in multiple batches).
   *
   * The purpose of creating a batch is to get a preliminary view of the files to be
   * uploaded. The batch may be abandoned if desired.
   *
   * The [limit] is the maximum number of files to include in the batch.
   * To obtain the total set files to be uploaded, create a batch with no limit.
   */
  fun createBatch(limit: Int = 0): UploadBatch {
    var files = localFiles.filter { !remote.hasFile(it) }
    if (limit > 0) files = files.take(limit)
    return UploadBatch(files)
  }

  /**
   * Upload a [batch] of files.
   * Provide an optional [listener] to be informed of stats for each uploaded file.
   * This method returns cumulative [UploadStats] of files that were uploaded.
   *
   * This method does not check whether any of the files exist in the remote store.
   */
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

/**
 * Represents a set of files to be uploaded.
 */
data class UploadBatch(
    val files: List<File>
) {
  val count = files.size
  val bytes = Bytes(files.sumByLong { it.length() })
  fun eta(rate: Mbps) = bytes.eta(rate)
}

/**
 * Statistics for one file that has been uploaded.
 */
data class UploadStat(
    val file: File,
    val millis: Long
) {
  val bytes = Bytes(file.length())
  val time = Time(millis)
  val mbps = Mbps(bytes, time)
}

/**
 * Statistics for a set of files that have been uploaded.
 */
class UploadStats(val stats: List<UploadStat>) {
  val count = stats.size
  val bytes = Bytes(stats.sumByLong { it.bytes.bytes })
  val millis = stats.sumByLong { it.millis }
  val time = Time(millis)
  val mbps = Mbps(bytes, time)
}

/**
 * Helper class to track statistics for a configurable [historyCount] of uploaded files.
 */
class RateMonitor(val historyCount: Int = 0) {
  private val stats = mutableListOf<UploadStat>()
  fun add(stat: UploadStat) {
    stats.add(stat)
    if (historyCount > 0) while (stats.size > historyCount) stats.removeAt(0)
  }
  fun stats() = UploadStats(stats)
}