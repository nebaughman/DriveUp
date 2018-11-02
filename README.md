# DriveUp

Encrypted file backup to Google Drive.

**This project is not affiliated with Google. It uses the Google Drive API/SDK and Google OAuth to interact with your Google Drive account.**

## Overview

DriveUp uploads a set of local files to Google Drive, in encrypted form, for backup purposes.

The program can be executed repeatedly and will only upload files that do not yet appear in the target Google Drive path.

### Fair Warning

> Note: This started as a weekend project to serve my own specific needs (and learn a thing or two).

DriveUp supports a fairly specific scenario of backing up files to Google Drive, and is still rather technically-oriented. 

* You'll need to be comfortable with command-line applications (and maybe even reading a bit of code) to use DriveUp.

* Additionally, at present, you will need to supply your own _application credentials_ (see below).

### Application Credentials

Applications that connect to Google Drive must supply _application credentials_ (`client_secret.json`), which identifies the source of the application. These credentials are obtained by the application developer, and are _usually_ bundled within an app.

However, DriveUp does **not** include these application credentials. You must register as a Google Drive API developer to use DriveUp.

> Once DriveUp is more stable, application credentials might be bundled with the release versions.

### Drive Access

DriveUp can be initialized with full, path-based, or read-only access to Drive. Path-based access grants read and write access only to folders and files created by DriveUp.

Access rights are configured during credentials initialization and summarised during the Google OAuth authentication process. These access rights are part of the credentials file created during initialization.

> More testing/documentation of Drive access and security is needed here, but 

### File Encryption

Files are encrypted to the user's public key prior to upload. DriveUp does not (yet) fetch files from Google Drive, nor decrypt them. Therefore, the user's private key is not needed (or used).

> To restore files, you would need to manually download the GPG-encrypted files and use another program (such as [`gnupg`](https://gnupg.org/)) to decrypt them with your private key.

> A future version of DriveUp may allow you to create a key pair. For now, BYOK (bring your own key).

## Running

DriveUp is a command-line only application, bundled into a JAR file. The [Java Runtime Environment (JRE)](https://java.com/) is required (not included) to execute the application JAR file. Run DriveUp like this:

```bash
java -jar driveup.jar [OPTIONS] COMMAND [ARGS]
```

> `driveup.jar` is obtained from the release and might include the version in the file name (eg, `driveup-0.2.jar`). Or, you may clone this project and build the jar yourself -- _viva la open source!_

Options always come before the command name. The primary option is `--config` which names the credentials file created by the `init` command.

* Use `java -jar driveup.jar --help` for an overview of OPTIONS and a list of available COMMANDS

* Use `java -jar driveup.jar COMMAND --help` for command-specific options

### Init

When first running DriveUp, execute the `init` command to import your GPG public key, configure GPG encryption recipient, perform Google OAuth, and store the resulting credentials file.

```bash
java -jar driveup.jar --config ~/path/to/driveup.creds init ...
```

DriveUp (via Google Drive API & SDK) uses Google OAuth to authenticate the user and grant authorization to use the user's Google Drive service. A browser window will be opened to a Google OAuth form. Google Drive account access must be granted by the user. Afterward, credentials are stored in the specified config file (`driveup.creds` by default) for subsequent executions.

> You may revoke access to the app in your Google account, which invalidates the stored credentials.
>
> If you delete the saved credentials file, you will need to authenticate to Google and authorize access to the app once again.

DriveUp uses the Google Drive API, which requires app registration. To use DriveUp, you must register as a Google developer and obtain your own `client_secret.json` application credentials.

### JSON

The `json` command prints a semi-human-readable version of your credentials file, mostly for verification/debugging purposes:

```bash
java -jar driveup.jar --config driveup.creds json
```

> This command is likely to be renamed in a future release.

### List

The `list` command lists remote files:

```bash
java -jar driveup.jar --config driveup.creds list
```

Note that this will only include files accessible to the credentials and access rights configured during initialization.

### Upload

The `upload` command encrypts local files and sends them to your Google Drive. Use `java -jar driveup.jar upload --help` to see options.

* `--upload-limit N` will upload at most _N_ files. 

* `--just-check` prevents any actual uploading; just shows summary of what would be uploaded. This is a useful flag for testing the waters. This is like a dry run, but _does_ connect to your Google Drive account to scan remote files and _will_ create the (empty) remote path if it does not exist.

> More details to be documented... For now, kindly refer to the source code!

As each file is uploaded, statistics are shown, including the estimated remaining time for the current _batch_ (see `--upload-limit`) and full set of files. For example:

```text
EXAMPLE.jpg: 2.2 MB / 4s = 4.3 Mbps | Batch remaining: 7 (24.6 MB) @ 3.3 Mbps = 59s | Total remaining: 1667 (5663.5 MB) @ 3.3 Mbps = 3h49m37s
```

Notice that source files are encrypted prior to upload and given a `.gpg` file extension. The above example file would appear as `EXAMPLE.jpg.gpg` in Google Drive with MIME type `application/pgp-encrypted`.

> DriveUp does _not_ encrypt file names! See other limitations below.

## Limitations

... hopefully to be improved in future versions

* Not very flexible (built with many simplifying assumptions)
* No (substantive) unit testing
* Insufficient error handling (if something crashes, check the state of things and try again)
* Large memory consumption for large files (file fully encrypted to memory before uploading)
* One-at-a-time, single-stream-per-file uploading
* Does not recurse into subdirectories
* Can limit number of files to send in a batch, but cannot limit maximum amount of data to send (or time to spend, or bandwidth to consume)
* Does not know how much space you have available in Google Drive (cannot warn you if your drive is full or has insufficient space for requested files)
* Remote files checked by name only (cannot detect files with changed local content; would need a manifest file with local hashes, ala duplicity)
* Stored file names are not obscured/encrypted (because this is how DriveUp identifies files)
* Cannot (yet) upload without encryption

## Technical

* Written in the [Kotlin](https://kotlinlang.org/) programming language
* Uses [Google Drive API/SDK](https://developers.google.com/drive/)
* Uses [BouncyGPG](https://github.com/neuhalje/bouncy-gpg) (and [BouncyCastle](https://bouncycastle.org/)) for GPG/PGP encryption
* Local data (credentials file) saved with [Protocol Buffers](https://developers.google.com/protocol-buffers/)
* Uses [Clikt](https://ajalt.github.io/clikt/) command-line parsing library
* The project is built with the [Gradle](https://gradle.org/) build tool
* Code is maintained with [Git](https://git-scm.com/) revision control system
* Git repository hosted by [GitHub](https://github.com/)

> I use the [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community Edition) development environment

### Protoc

Gradle is used to build the project, but if the [Protocol Buffers](https://developers.google.com/protocol-buffers/) `creds.proto` file is changed, you must manually run `protoc`:

> protoc --proto_path=src/main/proto/ --java_out=src/main/kotlin src/main/proto/*.proto

> TODO: Use the [Protocol Buffer Gradle Plugin](https://github.com/google/protobuf-gradle-plugin) to make protobuf compiling part of the gradle build process.

### Error Recovery

There are no guarantees, but here is a description of an observed failure case. 

While uploading a batch of files, a `java.net.SocketTimeoutException` was thrown. This stopped the upload process. The last file that had been logged had been uploaded (correctly). The _next_ file in the batch had **not** yet been logged (no stats for this file); nonetheless, the file **was** uploaded (and verified to be correct).

In this particular case, DriveUp could be run again, and resumed where it left off with no further error recovery needed.

Speculatively, the Google Drive API/SDK will discard any failed partial upload (eg, rather than including the partial file in your Drive).

## License

Heed the [License](LICENSE.txt).
