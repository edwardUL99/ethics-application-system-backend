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
The only configuration property for this module is defined in [files.ethics.properties](src/main/resources/files.ethics.properties).
The `files.upload-dir` is the name of the directory where files should be uploaded to on the server. This is also where
files are retrieved from