# PicUp

Encrypted photo backup to Google Drive.

## Fair Warning

PicUp is not (yet?) very user friendly. It supports a fairly specific scenario of backing up files to Google Drive. 

If you're not comfortable with command-line applications (and maybe reading a bit of code), PicUp is probably not for you.

For one thing, it requires you to register as a Google Drive API developer to use it (ie, `client_secret.json` is not included, see Setup).

> This was started as a weekend project, designed to serve my own specific needs (and learn a thing or two).

## Overview

PicUp uploads a set of local image files to Google Drive, in encrypted form, for backup purposes.

The program can be executed repeatedly and will only upload files that do not yet appear in the target Google Drive path. 

Files are encrypted to a user's public key prior to upload. PicUp does not (yet) fetch files from Google Drive, nor decrypt them (user's private key is not needed or used).

> To restore files, you would need to manually download the GPG-encrypted files and use another program (such as `gnupg`) to decrypt them with your private key.

PicUp is configured to only have access to Google Drive folders and files that it has created. These access rights are summarised during the Google OAuth authentication process, which is performed upon first execution (see Setup).

No temporary space is used. File encryption is performed in-process and streamed to Google Drive.

## Setup

PicUp uses the GDriver library (a thin wrapper around Google Drive API). GDriver requires Google Drive API app registration (not included). Registering as a Google Drive API developer provides `client_secret.json`, needed by GDriver.

PicUp (via GDriver, via Google Drive API) uses Google OAuth to authenticate the user and grant authorization to use the user's Google Drive service. When first running PicUp, if stored credentials are not available, a browser will be opened to an authentication and authorization URL. Google Drive account access must be granted by the user. Afterward, credentials are stored in a local file for subsequent executions.

> You may revoke access to this program in your Google account, which invalidates the stored credentials (you'll have to authorize the app once again).
>
> Similarly, if you delete the `StoredCredentials` file, you will need to authenticate to Google and authorize access to the app once again.

## Running

PicUp is a command-line only application, bundled into a JAR file. The Java Runtime Environment (JRE) is required (not included). Run PicUp like this:

```bash
java -jar picup-VERSION-all.jar [OPTIONS]
```

Use `--help` to see options. In brief:

* The user's GPG public key file and recipient identifier (eg, email address) must be specified.
* The local _root_ and _child_ path of your images must be given. The _child_ directory will be reproduced in the remote file store.

There are a number of other options that can be set... _(kindly refer to the source code)_

As each file is uploaded, statistics are shown, including the estimated remaining time for the current _batch_ (see `--upload-limit`) and full set of images. For example:

```text
IMG_20180421_144942.jpg: 2.2 MB / 4s = 4.3 Mbps | Batch remaining: 7 (24.6 MB) @ 3.3 Mbps = 59s | Total remaining: 1667 (5663.5 MB) @ 3.3 Mbps = 3h49m37s
```

Notice that source files are encrypted prior to upload and given a `.gpg` file extension. The above example file would appear as `IMG_20180421_144942.jpg.gpg` in Google Drive.

## Example

Suppose your files are stored in `~/pics/Pixel`. Your GPG key is available in `public_key.asc` (the default) and your GPG key identifier is `you@example.com`. The default (if not specified) `--remote-root` directory is `PicsBackup`.

The following will create the remote path `PicsBackup/Pixel` (in your Google Drive account) and upload all image files from local `pics/Pixel`, encrypting them using `you@example.com`'s public key.

`--upload-limit N` will upload only N files. `--just-check` prevents any actual uploading; just shows summary of what would be uploaded.

```bash
java -jar picup-1.0-all.jar --encryption-recipient=you@example.com --local-root ~/pics/ --local-child Pixel --upload-limit 10 --just-check
```

## Technical

* Written in the Kotlin programming language, for the JRE
* Uses GDriver, a thin wrapper around Google Drive API
* Uses BouncyGPG (and BouncyCastle) for GPG/PGP encryption
* Uses CliKt command-line parsing library

## Limitations

... hopefully to be improved in future versions

* Not very flexible (built with many simplifying assumptions)
* No unit testing
* No error handling (if something crashes, check the state of things and try again)
* Large memory consumption for large files (file fully encrypted to memory before uploading)
* One-at-a-time, single-stream-per-file uploading
* Only uploads ".jpg" files in the given path (not any other files or folders)
* Can limit number of images to send in a batch, but cannot limit maximum amount of data to send
* PicUp does not know how much space you have available in Google Drive (cannot warn you if your drive is full)

