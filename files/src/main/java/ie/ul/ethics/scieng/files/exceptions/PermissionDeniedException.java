package ie.ul.ethics.scieng.files.exceptions;

/**
 * This exception represents an exception when a user tries to access a file they do not have access to
 */
public class PermissionDeniedException extends FileException {
    /**
     * Create a PermissionDenied with the provided message and causing throwable
     *
     * @param message   the message to display
     * @param throwable the throwable that caused this exception
     */
    public PermissionDeniedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
