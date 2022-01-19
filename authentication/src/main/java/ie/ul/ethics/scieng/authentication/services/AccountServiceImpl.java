package ie.ul.ethics.scieng.authentication.services;

import ie.ul.ethics.scieng.authentication.exceptions.EmailExistsException;
import ie.ul.ethics.scieng.authentication.exceptions.IllegalUpdateException;
import ie.ul.ethics.scieng.authentication.exceptions.UsernameExistsException;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.authentication.models.ConfirmationToken;
import ie.ul.ethics.scieng.authentication.repositories.AccountRepository;
import ie.ul.ethics.scieng.authentication.repositories.ConfirmationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
@CacheConfig(cacheNames = "accounts")
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
     * The repository for retrieving and creating tokens
     */
    private final ConfirmationTokenRepository tokenRepository;

    /**
     * Instantiate an AccountServiceImpl with the provided dependencies
     * @param accountRepository the account repository to access accounts with
     * @param passwordEncoder the encoder for encoding passwords
     * @param tokenRepository the repository used for creating and retrieving tokens
     */
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, @Lazy PasswordEncoder passwordEncoder, ConfirmationTokenRepository tokenRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
    }

    /**
     * Create an account with the provided username, email and password. The password will be encrypted
     *
     * @param username the username to be associated with the account
     * @param email    the email associated with the account
     * @param password the plain text password to be encrypted
     * @param confirm true to confirm the account, false if not
     * @return the created account
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "account", allEntries = true),
            @CacheEvict(value = "userdetail", allEntries = true)
    })
    public Account createAccount(String username, String email, String password, boolean confirm) {
        if (getAccount(username, false) != null)
            throw new UsernameExistsException(username);
        else if (getAccount(email, true) != null)
            throw new EmailExistsException(email);

        password = passwordEncoder.encode(password);

        Account account = new Account(username, email, password, confirm);
        accountRepository.save(account);

        return account;
    }

    /**
     * Delete the provided account from the system
     *
     * @param account the account to delete
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "account", allEntries = true),
            @CacheEvict(value = "userdetail", allEntries = true)
    })
    public void deleteAccount(Account account) {
        accountRepository.delete(account);
    }

    /**
     * Authenticates the account with the provided password
     *
     * @param account  the account to authenticate
     * @param password the password to authenticate with. Should be a raw password
     * @return true if valid credentials, false otherwise
     */
    @Override
    public boolean authenticateAccount(Account account, String password) {
        return passwordEncoder.matches(password, account.getPassword());
    }

    /**
     * Update the account. The provided account should only have the email or password changed and not the username
     *
     * @param account the account with updated details (other than username)
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = "account", allEntries = true),
            @CacheEvict(value = "userdetail", allEntries = true)
    })
    public void updateAccount(Account account) {
        String username = account.getUsername();
        String email = account.getEmail();

        account.setPassword(passwordEncoder.encode(account.getPassword()));

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
    @Cacheable(value = "account")
    public Account getAccount(String username) {
        return accountRepository.findByUsername(username).orElse(null);
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
    @Cacheable(value = "account")
    public Account getAccount(String username, boolean email) {
        Optional<Account> optional = (email) ? accountRepository.findByEmail(username):accountRepository.findByUsername(username);

        return optional.orElse(null);
    }

    /**
     * Loads the user (account) by username into UserDetails. This is done to be compatible with Spring's UserDetailsService type
     * @param username the username to use
     * @return the user details object
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    @Cacheable(value = "userdetail")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = getAccount(username);

        if (account == null)
            throw new UsernameNotFoundException(username);

        return new User(username, account.getPassword(), new ArrayList<>());
    }

    /**
     * Generate a confirmation token for the provided account
     *
     * @param account the account to generate the token for
     * @return the created confirmation token
     */
    @Override
    public ConfirmationToken generateConfirmationToken(Account account) {
        ConfirmationToken token = new ConfirmationToken(account.getEmail());
        tokenRepository.save(token);

        return token;
    }

    /**
     * Attempt to confirm the provided account. If a ConfirmationToken exists for the account and it equals the provided
     * token, the account will be confirmed and the token deleted. Otherwise, false will be returned. Account.confirmed
     * will be set to true
     *
     * @param account the account to confirm
     * @param token   the token to confirm
     * @return true if confirmed, false if not
     */
    @Override
    public boolean confirmAccount(Account account, String token) {
        ConfirmationToken confirmationToken = tokenRepository.findByEmail(account.getEmail()).orElse(null);

        if (confirmationToken != null && confirmationToken.getToken().equals(token)) {
            account.setConfirmed(true);
            accountRepository.save(account); // update the confirmation status
            tokenRepository.delete(confirmationToken);

            return true;
        }

        return false;
    }
}