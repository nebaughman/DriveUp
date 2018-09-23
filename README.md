# DriveUp

Encrypted file backup to Google Drive.

## Fair Warning

Heed the [License](LICENSE.txt).

DriveUp is not (yet?) very user friendly. It supports a fairly specific scenario of backing up files to Google Drive. 

If you're not comfortable with command-line applications (and maybe reading a bit of code), DriveUp is probably not for you.

For one thing, it requires you to register as a Google Drive API developer to use it (ie, `client_secret.json` is not included, see Setup).

> This is really just a weekend project, designed to serve my own specific needs (and learn a thing or two).

## Overview

DriveUp uploads a set of local files to Google Drive, in encrypted form, for backup purposes.

The program can be executed repeatedly and will only upload files that do not yet appear in the target Google Drive path. 

Files are encrypted to a user's public key prior to upload. DriveUp does not (yet) fetch files from Google Drive, nor decrypt them (user's private key is not needed or used).

> To restore files, you would need to manually download the GPG-encrypted files and use another program (such as [`gnupg`](https://gnupg.org/)) to decrypt them with your private key.

DriveUp is configured to only have access to Google Drive folders and files that it has created. These access rights are summarised during the Google OAuth authentication process, which is performed upon first execution (see Setup).

No temporary space is used. File encryption is performed in-process and in-memory and streamed to Google Drive.

## Setup

DriveUp uses the Google Drive API, which requires app registration. To use DriveUp, you must register as a Google developer and obtain your own `client_secret.json` application credentials.

> The `client_secret.json` application credentials used to develop DriveUp are _not_ included.

DriveUp (via Google Drive API & SDK) uses Google OAuth to authenticate the user and grant authorization to use the user's Google Drive service. When first running DriveUp, if stored credentials are not available, a browser will be opened to an authentication and authorization URL. Google Drive account access must be granted by the user. Afterward, credentials are stored in a local file for subsequent executions.

> You may revoke access to the app in your Google account, which invalidates the stored credentials (you'll have to authorize the app once again).
>
> Similarly, if you delete the generated `StoredCredential` file, you will need to authenticate to Google and authorize access to the app once again.

## Running

DriveUp is a command-line only application, bundled into a JAR file. The [Java Runtime Environment (JRE)](https://java.com/) is required (not included) to execute the application JAR file. Run DriveUp like this:

```bash
java -jar driveup-VERSION-all.jar [OPTIONS] COMMAND [ARGS]
```

The application is split up into _commands_, each of which perform a certain task.

* Use `java -jar driveup-VERSION-all.jar --help` for an overview of common OPTIONS (needed by every COMMAND) and a list of available COMMANDS
* Use `java -jar driveup-VERSION-all.jar COMMAND --help` for command-specific options

### List

The `list` command lists remote files, which are accessible to the granted Google Drive credentials. If you have not yet established your credentials, the authentication process will be triggered (as described in Setup).

### Upload

Use `java -jar driveup-VERSION-all.jar upload --help` to see options. In brief:

* Your GPG public key file and recipient identifier (eg, email address) must be specified
* The local _root_ and _child_ path of your files must be given. The _child_ directory will be reproduced in the remote file store

> Note: File/path options are likely to change in a future version

There are a number of other options that can be set... _(kindly refer to the source code)_

As each file is uploaded, statistics are shown, including the estimated remaining time for the current _batch_ (see `--upload-limit`) and full set of files. For example:

```text
IMG_20180421_144942.jpg: 2.2 MB / 4s = 4.3 Mbps | Batch remaining: 7 (24.6 MB) @ 3.3 Mbps = 59s | Total remaining: 1667 (5663.5 MB) @ 3.3 Mbps = 3h49m37s
```

Notice that source files are encrypted prior to upload and given a `.gpg` file extension. The above example file would appear as `IMG_20180421_144942.jpg.gpg` in Google Drive with MIME type `application/pgp-encrypted`.

### Example

Suppose your files are stored in `~/pics/MyPics`. `credentials/client_secret.json` holds your app's identification. Your GPG key is available in `credentials/public_key.asc` (the default) and your GPG key identifier is `you@example.com`. The default (if not specified) `--remote-root` directory is `DriveUp`.

The following will create the remote path `DriveUp/MyPics` in your Google Drive account and upload all `.jpg` and `.mp4` files from local `~/MyPics`, encrypting them using `you@example.com`'s public key.

```bash
java -jar driveup-1.0-all.jar upload --encryption-recipient you@example.com --local-root ~/pics/ --local-child MyPics --file-extensions jpg,mp4 --upload-limit 10 --just-check
```

* `--upload-limit N` will upload at most _N_ files. 

* `--just-check` prevents any actual uploading; just shows summary of what would be uploaded. This is like a dry run, but _does_ connect to your Google Drive account to scan remote files and _will_ create the (empty) remote path if it does not exist. This is a useful flag for testing the waters.

## Technical

* Written in the [Kotlin](https://kotlinlang.org/) programming language
* Uses [Google Drive API/SDK](https://developers.google.com/drive/)
* Uses [BouncyGPG](https://github.com/neuhalje/bouncy-gpg) (and [BouncyCastle](https://bouncycastle.org/)) for GPG/PGP encryption
* Uses [Clikt](https://ajalt.github.io/clikt/) command-line parsing library
* The project is built with the [Gradle](https://gradle.org/) build tool
* Code is maintained with the [Git](https://git-scm.com/) revision control system
* Git repository hosted by [GitHub](https://github.com/)

> I use the [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community Edition) development environment

## Limitations

... hopefully to be improved in future versions

* Not very flexible (built with many simplifying assumptions)
* No (substantive) unit testing
* No error handling (if something crashes, check the state of things and try again)
* Large memory consumption for large files (file fully encrypted to memory before uploading)
* One-at-a-time, single-stream-per-file uploading
* Does not recurse into subdirectories
* Can limit number of files to send in a batch, but cannot limit maximum amount of data to send (or time to spend)
* Does not know how much space you have available in Google Drive (cannot warn you if your drive is full or has insufficient space for requested files)
* OAuth is triggered when Google Drive API logic does not find the `StoredCredential` file; it would be better if OAuth process and storage of credentials could be more explicitly controlled
* Remote files checked by name only (cannot detect files with changed local content; would need a manifest file with local hashes, ala duplicity)
* Stored file names are not obscured/encrypted (because this is how DriveUp identifies files)
* Cannot (yet) upload without encryption

## Error Recovery

There are no guarantees, but here is a description of an observed failure case. 

While uploading a batch of files, a `java.net.SocketTimeoutException` was thrown. This stopped the upload process. The last file that had been logged had been uploaded. The _next_ file in the batch had **not** yet been logged (no stats for this file); nonetheless, the file **was** uploaded. Downloading and verifying the contents showed that it was fully uploaded and its contents were correct (identical to original source file after decrypting).

In this particular case, DriveUp could be run again, and resumed where it left off with no further error recovery needed.

Speculatively, the Google Drive API/SDK will discard any failed partial upload (eg, rather than including the partial file in your Drive).

## FAQ

Q: Where do I find the executable jar file? 

> A: Build it yourself! (... I plan to provide this in the distribution in the future)

Q: Should I rely on this for important file backups?

> A: No! DriveUp is very preliminary and not well tested. Consider yourself warned (and heed the [License](LICENSE.txt)).
