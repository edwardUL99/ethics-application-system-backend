package ie.ul.ethics.scieng.users.authorization;

/**
 * This represents the request method that the permissions are being registered for in the context of user role
 * authorization
 */
public enum RequestMethod {
    /**
     * Represents permissions required for all request methods
     */
    ALL,
    /**
     * Represents the GET method that the permissions are required for
     */
    GET,
    /**
     * Represents the POST method that the permissions are required for
     */
    POST,
    /**
     * Represents the PUT method that the permissions are required for
     */
    PUT,
    /**
     * Represents the PATCH method that the permissions are required for
     */
    PATCH,
    /**
     * Represents the DELETE method that the permissions are required for
     */
    DELETE
}
