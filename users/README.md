# Ethics Application System Backend users Module
This module provides the backend functionality for user management. A user contains an account (which is registered through
the [authentication](../authentication) module) and other information such as their name and department they are based in. The module also
provides user role management.

Roles allow a set of permissions to be granted to a user with that role. Permissions enable users to access resources that
require a certain permission(s) to access it. API endpoints can be configured to require certain permissions to access them.

The module intercepts any requests and checks if the path requested is locked with permissions. If so, the user's permissions
are checked against the permissions and if they have the required permissions, they are authorized to access the resource.
Otherwise, the request is responded to with status code 401 and error message `insufficient_permissions`.

## Endpoints
All the endpoints in this module begin with the path `/api/users`. If the endpoint in the following table is [blank], it means
the request is just made with '/api/users' and no extra elements in the path

| Endpoint     | Method | Description                                                                                                                                                                                        |
|--------------|--------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [blank]             | GET    | When a get request is made to /users without any additional endpoints specified, a list of all the users in the system is returned (a shortened version of user profiles)                              |
| /user        | GET    | Retrieves the user with the specified username as a URL parameter. A URL boolean parameter email can also be specified to treat username as an email and find the user for the given email address |
|              | POST   | Allows a user to create their own user profile. The initial role is Standard User unless the email matches the configured chair person email and no chair already exists.                          |
|              | PUT    | This endpoint allows a user to update their own user profile                                                                                                                                       |
| /admin/user  | POST   | This endpoint allows a user with Admin permission to create a user profile for any user                                                                                                            |
|              | PUT    | This endpoint allows a user with Admin permission to update a user profile for any user                                                                                                            |
| /user/role   | PUT    | This allows a user with Update Permissions permission to change the role of a user                                                                                                                 |
| /roles       | GET    | This returns a listing of all the defined roles in the system                                                                                                                                      |
| /permissions | GET    | This returns a listing of all the defined permissions in the system                                                                                                                                |
| /search      | GET    | Uses a query language to serach for users                                       |

## Configuration
There are 2 configuration files provided by this module:
* src/main/resources/users.ethics.properties
* src/main/resources/permissions.json

The properties in [users.ethics.properties](src/main/resources/users.ethics.properties) are as follows:
* `permissions.enabled`: This is by default set to true, however it can be set to false to disable permissions authorization. However, it should always be
 be set to true
* `permissions.chair`: This property defines the email of the initial Chair. This is required when the system is first setup
 so that when the chair person signs up with the specified email, they are automatically assigned the Chair role. If a user
 already exists, with the Chair role, this email is ignored.

The [permissions.json](src/main/resources/permissions.json) file allows the configuration of the paths that need to be locked behind permissions. The following
is an example:
```json
{
  "paths": [
    {
      "path": "/api/users/user/role/",
      "permissions": "GRANT_PERMISSIONS"
    },
    {
      "path": "/api/**/admin/**",
      "permissions": "ADMIN",
      "requireAll": true
    }
  ]
}
```
The file [PermissionsConfiguration.java](FYP/ethics-application-system/backend/users/src/main/java/ie/ul/ethics/scieng/users/config/PermissionsConfiguration.java) by default defines the paths
that are equivalent to the above JSON file.

The `path` can be an ANT path so that it will match any path that matches the pattern, or a concrete path. The permissions field
allows a comma-separated list of permissions to be defined. The names are the names of the constant permissions fields defined in
[Permissions.java](FYP/ethics-application-system/backend/users/src/main/java/ie/ul/ethics/scieng/users/authorization/Permissions.java).

A field `requestMethod` can be set with a String matching one of the request methods in the following file:
[RequestMethods.java](FYP/ethics-application-system/backend/users/src/main/java/ie/ul/ethics/scieng/users/authorization/RequestMethod.java) and specifies that authorization
should only be carried out if the request is made using that method. If not specified, the default is `ALL` which means any request
requires authorization.

The field `requireAll` (default `false`) defines that, if `true`, the user requires all of the specified permissions, else, at
least one of the permissions is enough to satisfy for authorization.
