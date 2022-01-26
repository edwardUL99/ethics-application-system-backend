package ie.ul.ethics.scieng.files.antivirus;

/**
 * This exception represents an exception that occurs when scanning for viruses
 */
public class AntivirusException extends RuntimeException {
    /**
     * Construct an exception with the provided error message
     * @param message the error message
     */
    public AntivirusException(String message) {
        super(message);
    }

    /**
     * Construct an exception with the provided error message and causing throwable
     * @param message the error message
     * @param throwable the throwable that caused this exception
     */
    public AntivirusException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
