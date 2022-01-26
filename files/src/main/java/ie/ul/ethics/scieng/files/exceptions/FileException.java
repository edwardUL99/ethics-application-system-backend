package ie.ul.ethics.scieng.files.exceptions;

/**
 * This exception represents an exception that can occur when interacting with the file system
 */
public class FileException extends RuntimeException {
    /**
     * Create a FileException with the provided message and causing throwable
     * @param message the message to display
     * @param throwable the throwable that caused this exception
     */
    public FileException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
