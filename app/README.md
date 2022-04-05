# Ethics Application System Backend app Module
This module is the main entrypoint into the backend. It groups all the backend modules to enable the endpoints to be made
available in the executable JAR file.

## Endpoints
This module does not provide any of its own endpoints. It's sole purpose is to start the backend application and scan
for endpoints defined in the modules.

## Configuration
The file [app.ethics.properties.sample](src/main/resources/app.ethics.properties.sample) **needs* to be renamed to
`app.ethics.properties` and the database details changed to match the database configured for the application.

You may also need to change the postgres dependency in the [Parent POM](../pom.xml)
to a different driver (e.g. MySQL) if using a different database