package ie.ul.ethics.scieng.applications.exceptions;

/**
 * This exception is thrown when mapping requests fails
 */
public class MappingException extends ApplicationException {
    /**
     * Create a MappingException with the provided message
     *
     * @param message the message to display
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Create a MappingException with the provided message and causing throwable
     *
     * @param message   the message to display
     * @param throwable the causing throwable
     */
    public MappingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
