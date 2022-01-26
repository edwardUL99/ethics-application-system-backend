package ie.ul.ethics.scieng.authentication.exceptions;

/**
 * This exception is thrown when the email already exists
 */
public class EmailExistsException extends AuthenticationException {
    /**
     * The email that already exists
     */
    private final String email;

    /**
     * Construct an exception with the provided email
     * @param email the email that already exists
     */
    public EmailExistsException(String email) {
        super("An account with the email " + email + " already exists");
        this.email = email;
    }

    /**
     * Retrieve the email that already exists
     * @return the email that already exists
     */
    public String getEmail() {
        return email;
    }
}
