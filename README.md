# Ethics Application System Backend
This repository is the location of the back-end (server) development work done for my CSIS Final Year Project implementing an Ethics Application Submission and Managament System for the Faculty of Science and Engineering's Ethics Committee

[![Java CI with Maven](https://github.com/edwardUL99/ethics-application-system-backend/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/edwardUL99/ethics-application-system-backend/actions/workflows/maven.yml)

## Requirements
The project requires the following tools installed on your local machine:
* Java 11 for runtime, JDK 11 for development
* Apache Maven 3.6.3
* Python 3. **optional** - only required if you wish to use the Python tools (API tester and backup utility)
* The `files` module requires **ClamAV**. See [Files README.md](files/README.md) for instructions on setting it up. It can also 
be disabled should the deployment not support it (not recommended). It is by default disabled ([ethics-env.sh](tools/ethics-env.sh) sets the environment disable variable), however should be enabled 
as soon as ClamAV is set up and configured

The `tools/` directory provides Bash scripts which work on Ubuntu linux. The build process is different on other operating
systems. On other systems like Windows, you will need to run the maven build commands and java run commands manually. You can take a look
at the scripts for the commands as examples.

## Quickstart
As long as the requirements are satisfied, you can run the following command as a "quickstart". It builds and starts the application
with an embedded in-memory database for quick setup without having to setup a persistent Postgres database.

Run from the root of the project

```bash 
tools/quickstart.sh
```

**Note**: Quickstart does not support the following features since they'll need to be configured (as below) to work:
* Account Confirmation - Accounts will always be confirmed automatically without sending a confirmation e-mail to the new user
* E-mail functionality - Email needs to be configured to set the SMTP server port, sender email and password. This can be configured
by environment variables below
* Antivirus scanning of uploaded files - ClamAV needs to be installed and configured
* Exporting of Applications - Applications will be exported on request, but the link to download them is sent via e-mail and e-mail
does not work in a quick-start environment

## Build
To build the backend, from the project root, simply run the following command:
```bash
tools/build.sh -DkipTests
```

This script first runs a Maven build of all the modules. It by default, runs all the tests, but to skip tests, pass in
`-DskipTests` at the end of the command.

## Database Setup
The application can be used with a Postgres or Embedded in memory database. It is preferred to use Postgres since this storage is
persistent.

### Postgres
To install Postgres, ensure it is installed on your machine. The steps to install it on Linux (Ubuntu) can be found here:
https://www.postgresqltutorial.com/postgresql-getting-started/install-postgresql-linux/.

Regardless of your environment, once Postgres is installed, ensure that you can access it as an administrator so that you can create
databases and users.

Once you have access to the `psql` command, run the following commands:
```bash
psql -U <username>
postgres=# CREATE DATABASE ethics;
postgres=# CREATE USER ethicsuser WITH ENCRYPTED PASSWORD 'testpass'; 
postgres=# GRANT ALL PRIVILEGES ON DATABASE ethics TO ethicsuser;
```

For the password choose a more secure password. In the [ethics-env.sh](tools/ethics-env.sh), set the following properties:
```
export DATABASE_URL=jdbc:postgresql://localhost:5432/ethics
export DATABASE_USER=ethicsuser
export DATABASE_PASS=testpass
```

### Embedded
To run the application with an embedded in-memory database, run the following command:
```tools/run.sh -Dspring.profiles.active=embedded```

## Run
After a build is completed, from the project root, start the backend by running the following command:
```bash
tools/run.sh
```
This script executes the executable JAR produced by the build script

## Modules
The backend is defined as a set of Maven modules, each providing their own functionality and endpoints. See the
appropriate module's README for the defined endpoints.

The modules are outlined as follows:

| Module                           | Purpose                                                                                                                                                                       |
|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [app](app)                       | Provides the main entrypoint into the backend application. Imports all the backend modules                                                                                    |
| [applications](applications)     | Provides the backend processing of ethics applications                                                                                                                        |
| [authentication](authentication) | Provides authentication (username/email and password authentication) for the backend API                                                                                      |
| [common](common)                 | Provides common classes/resources that can be shared between the modules, such as e-mail functionality                                                                                                  |
| [exporter](exporter)             | Provides the backend functionality for exporting applications to other file formats                                                                                           |
| [test-utils](test-utils)         | Provides utility classes for testing in the other modules                                                                                                                     |
| [users](users)                   | Provides the backend functionality for details surrounding the system's user and the management and authorization of their roles, e.g., standard user, committee member, etc. |
| [files](files)                   | Provides a means of uploading and downloading files to and from the backend server                                                                                            |

## Configuration
Each module provides its own `<module>/src/main/resources/<module>.ethics.properties` file and other configuration files which allow
configuration of that respective module.

See each module's README file for information on configuration for that module.

### Environment
There is a list of environment variables that can also be configured which can be useful for configuring it in deployment
without having to change the properties files. The environment variables take precedence over the application properties.

**Note**: Environment variables take precedence over properties set through the JVM properties (-D flag) or
property files

The list of supported environment variables are as follows:
* **ETHICS_FRONTEND_URL**: The URL base of where the front-end is running. Any pages, e.g. forgot-password is appended onto
this url
* **ETHICS_ANTIVIRUS_DISABLE**: The presence of this variable disables antivirus scanning
* **ETHICS_EMAIL_DISABLE**: The presence of this variable disables sending of emails from the back-end
* **ETHICS_EMAIL_FROM**: The e-mail to send emails from
* **ETHICS_EMAIL_HOST**: The host of the e-mail server to send e-mails with
* **ETHICS_EMAIL_PORT**: The port of the e-mail server
* **ETHICS_EMAIL_PASSWORD**: The password of the account e-mails are being sent from
* **ETHICS_EMAIL_DEBUG**: The presence of this variable enables email sending debugging
* **ETHICS_ALWAYS_CONFIRM**: The presence of this variable results in the automatic confirmation of all new accounts without requiring
confirmation intervention
* **ETHICS_JWT_SECRET**: This variable holds a secret key that is used to encrypt/decrypt JWT tokens
* **ETHICS_UNCONFIRMED_REMOVAL**: This variable specifies the number of days to pass after which unconfirmed accounts will be
removed
* **ETHICS_RESET_TOKEN_EXPIRY**: Specified the number of hours after which reset password tokens should be considered expired
* **ETHICS_CHAIR_EMAIL**: On initial setup of the system, if a user is created with this e-mail address, they are automatically assigned the Chair role
* **ETHICS_EMAIL_CONTACT**: Specify the e-mail of the committee member to contact (specified also by a property in the [common](common/src/main/resources/common.ethics.properties)
configuration). Displayed in e-mail footers

These variables can be set externally or added to the [ethics-env.sh](tools/ethics-env.sh) which is sourced by [run.sh](tools/run.sh)
on startup

## Testing

### Python Script
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

### Postman
[Postman](https://www.postman.com/) is an API platform used in this project to perform automated testing of the API by
mocking requests and testing the responses.

To test these for yourself, download and install Postman. Then, in the [tools/postman](tools/postman) folder, you will find a
file called `EthicsBackend.postman_collection.json`. Import this file into your Postman installation by opening the app
and click import. See this tutorial on [Importing Postman Collections](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/).

When imported, in the Collections tab (on the left of the window), you can click `Ethics Backend`. When you have that opened,
you can then click `Run` on the top bar of the collection's tab and it will execute all the requests and tests.

Or you can simply run the following commands:
```bash
sudo apt-get install nodejs npm
sudo npm install newman
newman run tools/postman/EthicsBackend.postman_collection.json
```

## Backup
A Python utility to perform backups of the database and the uploaded files through the `files` module is described
in the following [README.md](tools/backup/README.md). It has a single entrypoint and is configured through a YAML file.
This utility can be run manually or possibly be scheduled to run also

