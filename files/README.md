# Ethics Application System Backend files Module
This module is responsible for managing the upload and download of files to and from the API server. It abstracts access
to files to access through endpoints. The front-end can then access these files by downloading the files from the server.

## Endpoints
All endpoints for this module start with `/api/files/`

| Endpoint             | Method | Description                                                                                                                                                                                                                                                             |
|----------------------|--------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| /upload              | POST   | This method takes the parameters target, directory, and file. The request should be sent as multipart encoded. Target is the name for the uploaded file, where directory is the directory to store it inside. If null, it is stored in the root of the upload directory |
| /download/{filename} | GET    | Retrieves the file specified by the filename. By default, it retrieves from the root of the upload directory. The directory can be specified with a request parameter (after ?) directory=<dir-name>.                                                                   |

## Configuration
The configuration properties for this module are defined in [files.ethics.properties](src/main/resources/files.ethics.properties).
The `files.upload-dir` is the name of the directory where files should be uploaded to on the server. This is also where
files are retrieved from. The `files.supported-types` is a comma-separated list of supported MIME-types that can be uploaded
through the API.

### Antivirus
This module uses ClamAV to provide antivirus scanning of uploaded files. To install the antivirus, run the following steps:
```bash
sudo tools/clamav-install.sh
sudo cp tools/clamd.conf /etc/clamav/
sudo tools/clamav-update.sh
sudo tools/clamav-start.sh
```

The following utility allows interacting with the daemon:
```bash
sudo tools/clamav-daemon.sh start|stop|status|restart
```

In the properties file for the module, there are `files.antivirus.*` properties.
* enabled: Determines if antivirus scanning is enabled or disabled
* host: The hostname the ClamAV daemon is running on (default localhost)
* port: The port the ClamAV daemon is running on (default 3310)
* platform: The platform the ClamAV daemon is running on (default UNIX, can choose from WINDOWS or JVM_PLATFORM)

Or it can be disabled by passing -Dantivirus.disable in as a System property

#### Troubleshooting
If on startup of the application, you get an exception with the following message:
```
Caused by: ie.ul.ethics.scieng.files.antivirus.AntivirusException: Failed to sanity check the antivirus scanner ie.ul.ethics.scieng.files.antivirus.ClamAvAntivirusScanner@6101fd7d
```
it means that the antivirus provider (ClamAV) could not be reached. This could be because the daemon isn't running,
or the configuration details are not correct. Try running `sudo tools/clamav-daemon.sh start` and try starting the application
again. Otherwise, check that the configuration details are correct.