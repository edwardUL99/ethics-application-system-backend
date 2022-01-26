package ie.ul.ethics.scieng.authentication.exceptions;

/**
 * This exception is thrown when the username already exists
 */
public class UsernameExistsException extends AuthenticationException {
    /**
     * The username that already exists
     */
    private final String username;

    /**
     * Construct an exception with the provided username
     * @param username the username that already exists
     */
    public UsernameExistsException(String username) {
        super("A user with the username " + username + " already exists");
        this.username = username;
    }

    /**
     * Retrieve the username that already exists
     * @return the username that already exists
     */
    public String getUsername() {
        return username;
    }
}
