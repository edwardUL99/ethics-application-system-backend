package ie.ul.edward.ethics.users.repositories;

import ie.ul.edward.ethics.users.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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
}
