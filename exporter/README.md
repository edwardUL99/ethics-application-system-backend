# Ethics Application System Backend exporter Module
The exporter module is responsible for exporting applications into PDF files. It is currently only able to
export applications in primitive formatting as a prototype, but it is still a useful tool for backing up features.

## Endpoints
All endpoints in this module begin with the path `/api/export`. The endpoints are outlined below:

| Endpoint       | Method | Description                                                                                                                                       |
|----------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| /download/[filename]     | GET   | This is an open endpoint that allows the download of an exported file. The filename is the name of the archive containing the exported applications                                             |
| /single        | POST    | Requests the export of a single application specified by the query parameter `id`. Task completion, with download link is notified by email |
| /range         | POST    | Requests applications submitted within a date range of `start` and `end` query parameters in format `yyyy-MM-dd` to be exported. Like /single, completion is notified by e-mail |

## Configuration
Exported applications are stored on the server in the uploads directory configured by the [files](../files) module. See
the [files README](../files/README.md) for information.

The exported file download links are sent to the requesting users by e-mail, so see e-mail configuration information in the
[common](../common) module which provides email functionality for use in multiple modules.
