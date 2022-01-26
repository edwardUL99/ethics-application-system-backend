package ie.ul.ethics.scieng.applications.exceptions;

/**
 * This class represents an exception that occurs when attempting to parse an application
 */
public class ApplicationParseException extends ApplicationException {
    /**
     * Create an ApplicationParseException
     * @param message the message to display
     */
    public ApplicationParseException(String message) {
        this(message, null);
    }

    /**
     * Create an ApplicationParseException
     * @param message the message to display
     * @param throwable the throwable that caused this exception
     */
    public ApplicationParseException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
