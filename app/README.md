# Ethics Application System Backend app Module
This module is the main entrypoint into the backend. It groups all the backend modules to enable the endpoints to be made
available in the executable JAR file.

## Endpoints
This module does not provide any of its own endpoints. It's sole purpose is to start the backend application and scan
for endpoints defined in the modules.

## Configuration
The file [application.properties](src/main/resources/application.properties) reads the following environment variables to configure the database:
* DATABASE_URL: jdbc url for the database
* DATABASE_USER: username of the database user to login with
* DATABASE_PASS: password of the database to login with

You may also need to change the postgres dependency in the [Parent POM](../pom.xml)
to a different driver (e.g. MySQL) if using a different database