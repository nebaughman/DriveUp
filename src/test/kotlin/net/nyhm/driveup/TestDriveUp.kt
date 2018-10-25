package net.nyhm.driveup

import org.junit.Test

/**
 * These are not proper unit tests.
 * These are just a convenient way to test drive functionality.
 */
class TestDriveUp {
  @Test
  fun testMain() {
    main(arrayOf())
  }

  @Test
  fun testInit() {
    main(arrayOf(
        "init",
        "--client-secret=local/creds/client_secret.json",
        "--public-key=local/creds/public_key.asc",
        "--encryption-recipient=test@example.com",
        "--access=path",
        "--output=local/test.creds",
        "--overwrite"
    ))
    testJson()
  }

  @Test
  fun testJson() {
    main(arrayOf(
        "--config=local/test.creds",
        "json"
    ))
  }

  @Test
  fun testList() {
    main(arrayOf(
        "--config=local/test.creds",
        "list"
    ))
  }
}
