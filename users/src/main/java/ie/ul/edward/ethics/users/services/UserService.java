package ie.ul.edward.ethics.users.services;

import ie.ul.edward.ethics.users.exceptions.AccountNotExistsException;
import ie.ul.edward.ethics.users.models.User;

/**
 * This interface represents the user service for providing the user's business logic
 */
public interface UserService {
    /**
     * Load the user with the given username
     * @param username the username of the user to find
     * @return the user if found, null if not
     */
    User loadUser(String username);

    /**
     * Create a new user by loading the user's account and save it
     * @param user the user to create. Should be constructed using the {@link User#User(String, String, String)} constructor
     * @return the created user with loaded account
     * @throws AccountNotExistsException if no account exists for the user's username
     * @throws IllegalStateException if the account is not created using the {@link User#User(String, String, String)} constructor
     */
    User createUser(User user);
}
