package ie.ul.ethics.scieng.common.email.exceptions;

/**
 * This class represents an exception that can occur when sending emails
 */
public class EmailException extends RuntimeException {
    /**
     * Create an exception with the provided message and throwable
     * @param message the message to display
     * @param throwable the throwable causing this exception
     */
    public EmailException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
