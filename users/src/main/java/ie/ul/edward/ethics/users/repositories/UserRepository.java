package ie.ul.edward.ethics.users.repositories;

import ie.ul.edward.ethics.users.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This interface represents the repository for interacting with user repositories
 */
@Repository
public interface UserRepository extends CrudRepository<User, String> {
    /**
     * Finds the user using the provided username.
     * Equivalent to findById
     * @param username the username to find the user with
     * @return the found user, or empty if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a list of users with the provided name
     * @param name the name of the role
     * @return the list of users
     */
    List<User> findByRole_Name(String name);

    /**
     * Find the user based on their account email
     * @param email the email to find the user with
     * @return the user if found, empty if not
     */
    Optional<User> findByAccount_Email(String email);
}
