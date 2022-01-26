package ie.ul.ethics.scieng.applications.exceptions;

/**
 * This exception is thrown whenever an applications associated exception is thrown
 */
public class ApplicationException extends RuntimeException {
    /**
     * Create an ApplicationException with the provided message
     * @param message the message to display
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Create an ApplicationException with the provided message and causing throwable
     * @param message the message to display
     * @param throwable the causing throwable
     */
    public ApplicationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
