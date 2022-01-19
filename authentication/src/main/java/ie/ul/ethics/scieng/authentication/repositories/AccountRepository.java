package ie.ul.ethics.scieng.authentication.repositories;

import ie.ul.ethics.scieng.authentication.models.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface represents a CRUD Repository for Accounts
 */
@Repository
public interface AccountRepository extends CrudRepository<Account, String> {
    /**
     * Find the account with the provided username. Note that this is functionally equivalent to findById
     * @param username the username to fetch the account with
     * @return the optional containing the account, empty if not found
     */
    Optional<Account> findByUsername(String username);

    /**
     * Find the account associated with the provided email
     * @param email the email to fetch the account with
     * @return the optional containing the account, empty if not found
     */
    Optional<Account> findByEmail(String email);
}
