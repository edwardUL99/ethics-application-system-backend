package ie.ul.ethics.scieng.applications.exceptions;

/**
 * This exception represents an exception that is thrown if an application status is illegal for a given operation
 */
public class InvalidStatusException extends ApplicationException {
    /**
     * Create an InvalidStatusException with the provided message
     *
     * @param message the message to display
     */
    public InvalidStatusException(String message) {
        super(message);
    }

    /**
     * Create an InvalidStatusException with the provided message and causing throwable
     *
     * @param message   the message to display
     * @param throwable the causing throwable
     */
    public InvalidStatusException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
