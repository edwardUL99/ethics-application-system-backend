package ie.ul.ethics.scieng.exporter.exceptions;

/**
 * An exception for an error that occurred exporting applications
 */
public class ExportException extends RuntimeException {
    /**
     * Constructs a new exception
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval
     *
     */
    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
