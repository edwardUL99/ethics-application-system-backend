package ie.ul.ethics.scieng.authentication.exceptions;

/**
 * This exception is thrown whenever an exception is thrown related to authentication
 */
public class AuthenticationException extends RuntimeException {
    /**
     * Creates a default exception
     */
    public AuthenticationException() {
        super();
    }

    /**
     * Creates an exception with the provided message
     * @param message the message to display on the exception
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Creates an exception with the provided message and causing throwable
     * @param message the message to display on the exception
     * @param throwable the throwable that caused this exception
     */
    public AuthenticationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
