package ie.ul.edward.authentication.services;

import ie.ul.edward.authentication.exceptions.IllegalUpdateException;
import ie.ul.edward.authentication.models.Account;
import ie.ul.edward.authentication.exceptions.UsernameExistsException;
import ie.ul.edward.authentication.exceptions.EmailExistsException;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * This interface represents a service for handling accounts
 */
public interface AccountService extends UserDetailsService {
    /**
     * Create an account with the provided username, email and password. The password will be encrypted
     * @param username the username to be associated with the account
     * @param email the email associated with the account
     * @param password the plain text password to be encrypted
     * @return the created account
     * @throws UsernameExistsException if an account already exists with the provided username
     * @throws EmailExistsException if an account already exists with the provided email
     */
    Account createAccount(String username, String email, String password) throws UsernameExistsException, EmailExistsException;

    /**
     * Delete the provided account from the system
     * @param account the account to delete
     */
    void deleteAccount(Account account);

    /**
     * Update the account. The provided account should only have the email or password changed and not the username
     * @param account the account with updated details (other than username)
     * @throws IllegalUpdateException if the update results in the username or email being one that does not exist
     */
    void updateAccount(Account account) throws IllegalUpdateException;

    /**
     * Get the account represented by the given username.
     * An alias for getAccount(username, false)
     * @param username the username for the account
     * @return the account represented by the username, null if not found
     */
    Account getAccount(String username);

    /**
     * Get the account represented by the username. If email is true, the account's email
     * will be treated as the username
     * @param username the username/email to fetch the account with
     * @param email true to search with email, false to search with username
     * @return the found account, null if not found
     */
    Account getAccount(String username, boolean email);
}
