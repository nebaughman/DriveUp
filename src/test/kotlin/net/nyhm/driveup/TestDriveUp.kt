package net.nyhm.driveup

import org.junit.Test

class TestDriveUp {
  @Test
  fun testMain() {
    main(emptyArray())
  }

  @Test
  fun testInit() {
    main(arrayOf(
        "init",
        //"--help",
        "--client-secret=local/creds/client_secret.json",
        "--public-key=local/creds/public_key.asc",
        "--encryption-recipient=test@example.com",
        "--output=local/test.creds",
        "--overwrite"
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
