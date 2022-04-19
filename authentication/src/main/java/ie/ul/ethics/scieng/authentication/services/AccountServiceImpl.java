package ie.ul.ethics.scieng.authentication.services;

import ie.ul.ethics.scieng.authentication.exceptions.EmailExistsException;
import ie.ul.ethics.scieng.authentication.exceptions.IllegalUpdateException;
import ie.ul.ethics.scieng.authentication.exceptions.UsernameExistsException;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.authentication.models.ConfirmationToken;
import ie.ul.ethics.scieng.authentication.models.ResetPasswordToken;
import ie.ul.ethics.scieng.authentication.repositories.AccountRepository;
import ie.ul.ethics.scieng.authentication.repositories.ConfirmationTokenRepository;
import ie.ul.ethics.scieng.authentication.repositories.ResetPasswordTokenRepository;

import ie.ul.ethics.scieng.common.properties.PropertyFinder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

/**
 * The implementation of the AccountService interface
 */
@Service
@Log4j2
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
     * The repository used for storing/retrieving the reset password tokens
     */
    private final ResetPasswordTokenRepository resetTokenRepository;

    /**
     * The number of days after which unconfirmed accounts are removed
     */
    @Value("${auth.unconfirmed-removal:31}")
    private int unconfirmedRemoval;

    /**
     * Instantiate an AccountServiceImpl with the provided dependencies
     * @param accountRepository the account repository to access accounts with
     * @param passwordEncoder the encoder for encoding passwords
     * @param tokenRepository the repository used for creating and retrieving tokens
     * @param resetTokenRepository the repository used for storing/retrieving the reset password tokens
     */
    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, @Lazy PasswordEncoder passwordEncoder,
                              ConfirmationTokenRepository tokenRepository, ResetPasswordTokenRepository resetTokenRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.resetTokenRepository = resetTokenRepository;
    }

    @PostConstruct
    private void runScheduled() {
        this.purgeExpiredResetTokens();
        this.purgeUnconfirmedAccounts();
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
    public Account createAccount(String username, String email, String password, boolean confirm) {
        if (getAccount(username, false) != null)
            throw new UsernameExistsException(username);
        else if (getAccount(email, true) != null)
            throw new EmailExistsException(email);

        password = passwordEncoder.encode(password);

        Account account = new Account(username, email, password, confirm);

        return accountRepository.save(account);
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

    /**
     * Request a password reset for the provided account
     *
     * @param account the account to request the password reset for
     * @return the token used for resetting the password
     */
    @Override
    public ResetPasswordToken requestPasswordReset(Account account) {
        String hoursExpiryString = PropertyFinder.findProperty("ETHICS_RESET_TOKEN_EXPIRY", "auth.reset-token-expiry");
        int hoursExpiry = (hoursExpiryString == null) ? 2:Integer.parseInt(hoursExpiryString);

        ResetPasswordToken token = new ResetPasswordToken(account.getUsername(), UUID.randomUUID().toString(), LocalDateTime.now().plusHours(hoursExpiry));
        resetTokenRepository.save(token);

        return token;
    }

    /**
     * Verify the given password reset token against the given account. If the token does not exist, does not match or
     * expired, this should return false
     *
     * @param account the account to verify the token against
     * @param token   the token to verify
     * @return true if verified, false if not
     */
    @Override
    public boolean verifyPasswordResetToken(Account account, String token) {
        ResetPasswordToken savedToken = resetTokenRepository.findByUsername(account.getUsername()).orElse(null);

        if (savedToken != null) {
            if (!savedToken.isExpired()) {
                return savedToken.getToken().equals(token);
            } else {
                resetTokenRepository.delete(savedToken);
            }
        }

        return false;
    }

    /**
     * Reset the password of the account and remove any tokens that exist for the user
     *
     * @param account  the account to reset the password of
     * @param password the password to set for the account
     */
    @Override
    public void resetPassword(Account account, String password) {
        account.setPassword(passwordEncoder.encode(password));
        resetTokenRepository.deleteById(account.getUsername());
        accountRepository.save(account);
    }

    /**
     * A method that purges expired reset tokens. Not exposed by the AccountService API (interface). Just provided by
     * this implementation
     */
    @Transactional
    @Scheduled(cron = "${auth.scheduling.cron:0 0 5 * * ?}")
    public void purgeExpiredResetTokens() {
        this.resetTokenRepository.findAll()
            .forEach(token -> {
                if (token.isExpired())
                    this.resetTokenRepository.delete(token);
            });
    }

    /**
     * Purge the unconfirmed accounts. Another method not exposed by the public AccountService API as it is an implementation
     * detail that client code does not need to know about
     */
    @Transactional
    @Scheduled(cron = "${auth.scheduling.cron:0 0 5 * * ?}")
    public void purgeUnconfirmedAccounts() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(unconfirmedRemoval);

        this.tokenRepository.findAll()
            .forEach(token -> {
                if (token.getTimeCreated().isBefore(threshold)) {
                    Account account = this.accountRepository.findByEmail(token.getEmail()).orElse(null);

                    if (account != null && !account.isConfirmed()) {
                        log.info("Purging account with username {} as it is not confirmed and has exceeded the threshold to remove unconfirmed accounts", account.getUsername());
                        this.accountRepository.delete(account);
                    }

                    this.tokenRepository.delete(token);
                }
            });
    }
}
