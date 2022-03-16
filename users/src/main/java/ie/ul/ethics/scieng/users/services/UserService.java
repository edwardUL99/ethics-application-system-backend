package ie.ul.ethics.scieng.users.services;

import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.models.authorization.Role;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * This interface represents the user service for providing the user's business logic
 */
public interface UserService {
    /**
     * Retrieve all users in the system
     * @return the list of users
     */
    List<User> getAllUsers();

    /**
     * Load the user with the given username
     * @param username the username of the user to find
     * @return the user if found, null if not
     */
    User loadUser(String username);

    /**
     * Load the user with the given username
     * @param username the username to load the user with
     * @param email true if username is to be treated as email, false if username
     * @return the user if found, null if not
     */
    User loadUser(String username, boolean email);

    /**
     * Create a new user by loading the user's account and save it.
     * If the user's email matches the chair or administrator's email, they are assigned the chair or administrator role respectively
     * @param user the user to create. Should be constructed using the {@link User#User(String, String, String)} constructor
     * @return the created user with loaded account
     * @throws AccountNotExistsException if no account exists for the user's username
     * @throws IllegalStateException if the account is not created using the {@link User#User(String, String, String)} constructor
     */
    User createUser(User user);

    /**
     * This method updates the user
     * @param user the user the update
     * @throws IllegalStateException if the user's account has changed as the account must stay the same and not be
     * updated in this request
     * @throws AccountNotExistsException if there is no saved account for this user
     */
    void updateUser(User user);

    /**
     * This method updates the user's role. If the role is chair and a chair already exists, the existing chair is demoted
     * to a committee member
     * @param user the user to update
     * @param role the role to change
     */
    void updateRole(User user, Role role);

    /**
     * Search for users with the given specification
     * @param specification the specification to search with
     * @return the list of found users
     */
    List<User> search(Specification<User> specification);
}
