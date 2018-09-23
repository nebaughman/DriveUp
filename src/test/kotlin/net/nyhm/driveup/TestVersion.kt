package net.nyhm.driveup

import org.junit.Assert.*
import org.junit.Test

class TestVersion {
  @Test
  fun testVersion() {
    assertEquals("DEV", Version.version)
  }
}