package ie.ul.ethics.scieng.users.exceptions;

/**
 * This exception is thrown when trying to load a user where no account exists for that user
 */
public class AccountNotExistsException extends UsersException {
    /**
     * Creates an exception where an account does not exist for the provided username
     * @param username the username of the account that is supposed to exist
     */
    public AccountNotExistsException(String username) {
        super("No account for user " + username + " exists. Create an account for the user in authentication first");
    }
}
