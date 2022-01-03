package ie.ul.edward.ethics.common;

import org.springframework.stereotype.Component;

/**
 * This class provides constant values for the back-end
 */
@Component
public final class Constants {
    /**
     * The base of the url for the api, i.e. /api/<endpoint>
     */
    public static final String API_BASE = "api";

    /**
     * A key to use for a general message in the response body
     */
    public static final String MESSAGE = "message";

    /**
     * The key for response body error messages
     */
    public static final String ERROR = "error";

    /**
     * The error message for when username already exists
     */
    public static final String USERNAME_EXISTS = "username_exists";

    /**
     * The error message for when an email already exists
     */
    public static final String EMAIL_EXISTS = "email_exists";

    /**
     * The user is disabled
     */
    public static final String USER_DISABLED = "user_disabled";

    /**
     * Invalid login credentials given
     */
    public static final String INVALID_CREDENTIALS = "invalid_credentials";

    /**
     * A constant used for when an account is updated correctly
     */
    public static final String ACCOUNT_UPDATED = "account_updated";

    /**
     * The message used when sending an illegal update
     */
    public static final String ILLEGAL_UPDATE = "illegal_update";

    /**
     * An error message for when a user does not have sufficient permissions to pass through permissions authorization
     * for a resource
     */
    public static final String INSUFFICIENT_PERMISSIONS = "insufficient_permissions";

    /**
     * An error message for when a user is attempted to be created but the account doesn't exist for them
     */
    public static final String ACCOUNT_NOT_EXISTS = "account_not_exists";

    /**
     * This enum provides endpoint constants identifying the endpoints.
     * The endpoint name as seen in the /api/<endpoint> URL can be gotten by Endpoints.endpoint()
     */
    public enum Endpoint {
        /**
         * The endpoint providing the authentication functionality
         */
        AUTHENTICATION("auth"),
        /**
         * The endpoint providing user management functionality
         */
        USERS("users"),
        /**
         * The endpoint providing applications submission and management functionality
         */
        APPLICATIONS("applications"),
        /**
         * The endpoint providing exporting application functionality
         */
        EXPORT("export");

        /**
         * The endpoint name represented by the enum
         */
        private final String endpoint;

        /**
         * Create an enum object
         * @param endpoint the name of the endpoint in the URL
         */
        Endpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        /**
         * Return the endpoint name represented by the enum
         * @return the endpoint name
         */
        public String endpoint() {
            return endpoint;
        }
    }

    /**
     * Creates the API path for the provided endpoint. Additional args can be passed in, for example, AUTHENTICATION endpoint
     * can take login which results in /api/auth/login/
     * @param endpoint the endpoint to create the api path for
     * @param additionalArgs the additional path arguments to add
     * @return the created path
     */
    public static String createApiPath(Endpoint endpoint, String...additionalArgs) {
        String url = String.format("/%s/%s/", API_BASE, endpoint.endpoint());

        if (additionalArgs.length > 0) {
            String additional = String.join("/", additionalArgs);
            url += additional + "/";
        }

        return url;
    }
}
