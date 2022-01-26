package ie.ul.ethics.scieng.authentication.repositories;

import ie.ul.ethics.scieng.authentication.models.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface represents a repository for saving, finding and deleting ConfirmationTokens
 */
@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, String> {
    /**
     * Find the confirmation token if it exists for the provided email. Equivalent to findById
     * @param email the email to find the token for
     * @return the confirmation token if found, empty if not.
     */
    Optional<ConfirmationToken> findByEmail(String email);
}
