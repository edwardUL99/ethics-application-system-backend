# Ethics Application System Backend
This repository is the location of the back-end (server) development work done for my CSIS Final Year Project implementing an Ethics Application Submission and Managament System for the Faculty of Science and Engineering's Ethics Committee

[![Java CI with Maven](https://github.com/edwardUL99/ethics-application-system-backend/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/edwardUL99/ethics-application-system-backend/actions/workflows/maven.yml)

## Requirements
The project requires the following tools installed on your local machine:
* Java 11 for runtime, JDK 11 for development
* Apache Maven 3.6.3
* Python 3. **optional** - only required if you wish to use the `tools/apitest.py` tool for testing the backend API

## Modules
The backend is defined as a set of Maven modules, each providing their own functionality and endpoints. See the
appropriate module's README for the defined endpoints. 

The modules are outlined as follows:

| Module                           | Purpose                                                                                                                                                                       |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [app](app)                       | Provides the main entrypoint into the backend application. Imports all the backend modules                                                                                    |
| [applications](applications)     | Provides the backend processing of ethics applications                                                                                                                        |
| [authentication](authentication) | Provides authentication (username/email and password authentication) for the backend API                                                                                      |
| [common](common)                 | Provides common classes/resources that can be shared between the modules                                                                                                      |
| [exporter](exporter)             | Provides the backend functionality for exporting applications to other file formats                                                                                           |
| [test-utils](test-utils)         | Provides utility classes for testing in the other modules                                                                                                                     |
| [users](users)                   | Provides the backend functionality for details surrounding the system's user and the management and authorization of their roles, e.g., standard user, committee member, etc. |


## Build
To build the backend, from the project root, simply run the following command:
```bash
tools/build.sh
```
This script first runs a Maven build of all the modules (except for the **app** module). When this is completed, it builds
the **app** module and repackages it in a way that makes the JAR file executable.

## Run
After a build is completed, from the project root, start the backend by running the following command:
```bash
tools/run.sh
```
This script executes the executable JAR produced by the build script

## Testing
A Python script ``tools/apitest.py`` can be executed to make requests to the API backend. It is similar to using a tool
like Postman. The arguments it supports are as follows:
```
usage: apitest.py [-h] [-u URL] [-f FILE] [-e ENDPOINT] [-d DATA] [-H HEADERS] [-m {GET,POST,PUT,DELETE}]

Sends API requests to the back-end API to visualise the JSON response data

optional arguments:
  -h, --help            show this help message and exit
  -u URL, --url URL     The base URL where the API is hosted at
  -f FILE, --file FILE  A request JSON file
  -e ENDPOINT, --endpoint ENDPOINT
                        The endpoint to send the request to
  -d DATA, --data DATA  The JSON data to send to the API. Can be a JSON String or a JSON file
  -H HEADERS, --headers HEADERS
                        Headers as a JSON-like String {"Content-Type": "application/json"}
  -m {GET,POST,PUT,DELETE}, --method {GET,POST,PUT,DELETE}
                        The method to send the request with

```

The URL defaults to `http://localhost:8080`

You can run a request using command line arguments. The following is an example of registering a user using the authentication endpoint:
```bash
python3 tools/apitest.py -e /api/auth/register/ -d '{"username": "user", "email": "user@ul.ie", "password": "password"}' -m POST
```

You can also define a request in a JSON file to be passed in with the -f argument. An example of the above command-line invocation is as follows:
```json
{
  "endpoint": "api/auth/register/",
  "data": {
    "username": "eddylynch9",
    "email": "eddylynch9@gmail.com",
    "password": "testPass2"
  }, 
  "method": "POST"
}
```

Again, here `url` defaults to the same as command-line. Headers can be specified using `headers` and an object with the header name as the key

## Configuration
Each module provides its own `<module>/src/main/resources/<module>.ethics.properties` file and other configuration files which allow
configuration of that respective module. 

See each module's README file for information on configuration for that module.