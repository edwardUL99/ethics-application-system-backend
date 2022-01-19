package ie.ul.ethics.scieng.users.exceptions;

/**
 * Represents any exception that may be thrown by the users module
 */
public class UsersException extends RuntimeException {
    /**
     * Create an exception with the provided message
     * @param message the message to display on the exception
     */
    public UsersException(String message) {
        super(message);
    }

    /**
     * Create an exception with the provided message and causing throwable
     * @param message the message to display on the exception
     * @param throwable the causing throwable
     */
    public UsersException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
