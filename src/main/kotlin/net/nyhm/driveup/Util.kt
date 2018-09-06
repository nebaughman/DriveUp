package net.nyhm.driveup

import java.util.concurrent.TimeUnit

data class Bytes(val bytes: Long) {
  val bits = bytes * 8
  val kb = bytes / 1024.0
  val mb = kb / 1024.0
  val gb = mb / 1024.0

  val mbits = bits / 1024.0 / 1024.0

  fun eta(rate: Mbps) = Time(Math.round(mbits / rate.mbps * 1000))

  override fun toString() = when {
    gb > 1 -> format(gb) + " GB"
    mb > 1 -> format(mb) + " MB"
    kb > 1 -> format(kb) + " KB"
    else -> "$bytes bytes"
  }
}

data class Time(val millis: Long) {
  override fun toString(): String {
    var time = millis
    val h = TimeUnit.MILLISECONDS.toHours(time)
    time -= TimeUnit.HOURS.toMillis(h)
    val m = TimeUnit.MILLISECONDS.toMinutes(time)
    time -= TimeUnit.MINUTES.toMillis(m)
    val s = TimeUnit.MILLISECONDS.toSeconds(time)
    var str = ""
    if (h > 0) str += "${h}h"
    if (str.isNotEmpty() || m > 0) str += "${m}m"
    if (str.isNotEmpty() || s > 0) str += "${s}s"
    if (str.isEmpty()) str = "${millis}ms"
    return str
  }
}

data class Mbps(val bytes: Bytes, val time: Time) {
  constructor(bytes: Long, millis: Long): this(Bytes(bytes), Time(millis))
  val mbps = bytes.mbits / TimeUnit.MILLISECONDS.toSeconds(time.millis)
  override fun toString() = "${format(mbps)} Mbps"
}

fun format(num: Number) = "%.1f".format(num)

// https://stackoverflow.com/a/37537228/6004010
inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
  var sum = 0L
  for (element in this) {
    sum += selector(element)
  }
  return sum
}