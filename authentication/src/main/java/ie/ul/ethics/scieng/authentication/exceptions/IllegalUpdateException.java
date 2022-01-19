package ie.ul.ethics.scieng.authentication.exceptions;

/**
 * This exception is thrown when an illegal update of an account occurs (i.e. the update attempts to update an account
 * with a username or email address that doesn't exist
 */
public class IllegalUpdateException extends AuthenticationException {
    /**
     * Construct an IllegalUpdateException
     * @param username the username/email that will result in the update being illegal
     * @param email true if the username is an email, false if username
     */
    public IllegalUpdateException(String username, boolean email) {
        super("An attempt to update an account will result in the account having " +
                ((email) ? "an email (" + username + ") that does not exist": "a username (" + username + ") that does not exist"));
    }
}
