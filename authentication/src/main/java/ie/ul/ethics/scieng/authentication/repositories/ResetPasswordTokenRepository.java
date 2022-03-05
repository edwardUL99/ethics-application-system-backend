package ie.ul.ethics.scieng.authentication.repositories;

import ie.ul.ethics.scieng.authentication.models.ResetPasswordToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * This interface represents a repository used for storing/retrieving password reset tokens
 */
public interface ResetPasswordTokenRepository extends CrudRepository<ResetPasswordToken, String> {
    /**
     * Find the password reset token by username
     * @param username the username that the token should be found with
     * @return the found token, or empty optional if not found
     */
    Optional<ResetPasswordToken> findByUsername(String username);
}
