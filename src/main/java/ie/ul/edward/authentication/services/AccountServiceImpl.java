package ie.ul.edward.authentication.services;

import ie.ul.edward.authentication.exceptions.AuthenticationException;
import ie.ul.edward.authentication.exceptions.EmailExistsException;
import ie.ul.edward.authentication.exceptions.IllegalUpdateException;
import ie.ul.edward.authentication.exceptions.UsernameExistsException;
import ie.ul.edward.authentication.models.Account;
import ie.ul.edward.authentication.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

/**
 * The implementation of the AccountService interface
 */
@Service
public class AccountServiceImpl implements AccountService {
    /**
     * The account repository used for interacting with the accounts database
     */
    private final AccountRepository accountRepository;

    /**
     * The password encoder to use for encoding passwords
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Instantiate an AccountServiceImpl with the provided dependencies
     * @param accountRepository the account repository to access accounts with
     * @param passwordEncoder the encoder for encoding passwords
     */
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Create an account with the provided username, email and password. The password will be encrypted
     *
     * @param username the username to be associated with the account
     * @param email    the email associated with the account
     * @param password the plain text password to be encrypted
     * @return the created account
     */
    @Override
    public Account createAccount(String username, String email, String password) {
        if (getAccount(username, false) != null)
            throw new UsernameExistsException(username);
        else if (getAccount(email, true) != null)
            throw new EmailExistsException(email);

        password = passwordEncoder.encode(password);

        Account account = new Account(username, email, password);
        accountRepository.save(account);

        return account;
    }

    /**
     * Delete the provided account from the system
     *
     * @param account the account to delete
     */
    @Override
    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    /**
     * Update the account. The provided account should only have the email or password changed and not the username
     *
     * @param account the account with updated details (other than username)
     */
    @Override
    public void updateAccount(Account account) {
        String username = account.getUsername();
        String email = account.getEmail();

        if (getAccount(username, false) == null)
            throw new IllegalUpdateException(username, false);

        if (getAccount(email, true) == null)
            throw new IllegalUpdateException(email, true);

        accountRepository.save(account);
    }

    /**
     * Get the account represented by the given username
     *
     * @param username the username for the account
     * @return the account represented by the username, null if not found
     */
    @Override
    public Account getAccount(String username) {
        return getAccount(username, false);
    }

    /**
     * Get the account represented by the username. If email is true, the account's email
     * will be treated as the username
     *
     * @param username the username/email to fetch the account with
     * @param email    true to search with email, false to search with username
     * @return the found account, null if not found
     */
    @Override
    public Account getAccount(String username, boolean email) {
        Optional<Account> optional =
                (email) ? accountRepository.findByEmail(username):accountRepository.findByUsername(username);

        return optional.orElse(null);
    }

    /**
     * Loads the user (account) by username into UserDetails. This is done to be compatible with Spring's UserDetailsService type
     * @param username the username to use
     * @return the user details object
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = getAccount(username);

        if (account == null)
            throw new UsernameNotFoundException(username);

        return new User(username, account.getPassword(), new ArrayList<>());
    }
}
