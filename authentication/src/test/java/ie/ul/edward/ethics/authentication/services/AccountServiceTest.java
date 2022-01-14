package ie.ul.edward.ethics.authentication.services;

import ie.ul.edward.ethics.authentication.exceptions.EmailExistsException;
import ie.ul.edward.ethics.authentication.exceptions.IllegalUpdateException;
import ie.ul.edward.ethics.authentication.exceptions.UsernameExistsException;
import ie.ul.edward.ethics.authentication.models.Account;
import ie.ul.edward.ethics.authentication.models.ConfirmationToken;
import ie.ul.edward.ethics.authentication.repositories.AccountRepository;
import ie.ul.edward.ethics.authentication.repositories.ConfirmationTokenRepository;
import ie.ul.edward.ethics.test.utils.Caching;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import static ie.ul.edward.ethics.test.utils.constants.Authentication.*;
import static org.mockito.Mockito.*;

/**
 * This class provides unit tests for the Account Service
 */
@SpringBootTest(classes = {
        ie.ul.edward.ethics.test.utils.TestApplication.class,
        ie.ul.edward.ethics.authentication.test.config.TestConfiguration.class
})
public class AccountServiceTest {
    /**
     * The mocked account repository
     */
    @MockBean
    private AccountRepository accountRepository;
    /**
     * The mocked token repository
     */
    @MockBean
    private ConfirmationTokenRepository tokenRepository;
    /**
     * The mocked password encoder
     */
    @MockBean
    private PasswordEncoder passwordEncoder;
    /**
     * The account service being tested
     */
    @Autowired
    private AccountService accountService;
    /**
     * The cache utilities so we can evict cache for testing
     */
    @Autowired
    private Caching cache;

    /**
     * Clear cache before each test
     */
    @BeforeEach
    private void clearCache() {
        cache.clearCache();
    }

    /**
     * Creates an account for testing
     * @return the account for testing
     */
    public static Account createTestAccount() {
        return new Account(USERNAME, EMAIL, PASSWORD, false);
    }

    /**
     * Tests that an account should be created successfully
     */
    @Test
    public void shouldCreateAccount() {
        given(passwordEncoder.encode(PASSWORD))
                .willReturn(ENCRYPTED_PASSWORD);
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.empty());

        Account account = accountService.createAccount(USERNAME, EMAIL, PASSWORD, false);

        assertEquals(account.getUsername(), USERNAME);
        assertEquals(account.getEmail(), EMAIL);
        assertEquals(account.getPassword(), ENCRYPTED_PASSWORD);
        assertFalse(account.isConfirmed());

        verify(passwordEncoder).encode(PASSWORD);
        verify(accountRepository).findByUsername(USERNAME);
        verify(accountRepository).findByEmail(EMAIL);
    }

    /**
     * Tests that an account should be created successfully and be already confirmed
     */
    @Test
    public void shouldCreateAccountConfirmed() {
        given(passwordEncoder.encode(PASSWORD))
                .willReturn(ENCRYPTED_PASSWORD);
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.empty());

        Account account = accountService.createAccount(USERNAME, EMAIL, PASSWORD, true);

        assertEquals(account.getUsername(), USERNAME);
        assertEquals(account.getEmail(), EMAIL);
        assertEquals(account.getPassword(), ENCRYPTED_PASSWORD);
        assertTrue(account.isConfirmed());

        verify(passwordEncoder).encode(PASSWORD);
        verify(accountRepository).findByUsername(USERNAME);
        verify(accountRepository).findByEmail(EMAIL);
    }

    /**
     * Tests that an exception is thrown if a username already exists on createAccount
     */
    @Test
    public void shouldThrowWhenCreateWithExistingUsername() {
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(createTestAccount()));

        assertThrows(UsernameExistsException.class, () -> accountService.createAccount(USERNAME, EMAIL, PASSWORD, false));

        verify(accountRepository).findByUsername(USERNAME);
        verifyNoMoreInteractions(accountRepository);
    }

    /**
     * Tests that an exception is thrown if an email already exists on createAccount
     */
    @Test
    public void shouldThrowWhenCreateWithExistingEmail() {
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.of(createTestAccount()));

        assertThrows(EmailExistsException.class, () -> accountService.createAccount(USERNAME, EMAIL, PASSWORD, false));

        verify(accountRepository).findByUsername(USERNAME);
        verify(accountRepository).findByEmail(EMAIL);
    }

    /**
     * Tests that an account should be deleted
     */
    @Test
    public void shouldDeleteAccount() {
        Account account = createTestAccount();
        accountService.deleteAccount(account); // can only test that no error occurs in deleteAccount since it is just a delegate
        verify(accountRepository).delete(account);
    }

    /**
     * Tests that an account should be authenticated
     */
    @Test
    public void shouldAuthenticateAccount() {
        Account account = createTestAccount();

        given(passwordEncoder.matches(PASSWORD, account.getPassword()))
                .willReturn(true);

        boolean auth = accountService.authenticateAccount(account, PASSWORD);

        assertTrue(auth);
        verify(passwordEncoder).matches(PASSWORD, account.getPassword());
    }

    /**
     * Tests that an account should be updated
     */
    @Test
    public void shouldUpdateAccount() {
        Account account = createTestAccount();

        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(account));
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.of(account));
        given(passwordEncoder.encode(PASSWORD))
                .willReturn(ENCRYPTED_PASSWORD);

        assertDoesNotThrow(() -> accountService.updateAccount(account));
        assertEquals(account.getPassword(), ENCRYPTED_PASSWORD);
        verify(accountRepository).findByUsername(USERNAME);
        verify(accountRepository).findByEmail(EMAIL);
        verify(passwordEncoder).encode(PASSWORD);
    }

    /**
     * Tests that an illegal update should be thrown if the username does not exist
     */
    @Test
    public void shouldThrowIllegalUpdateOnNonExistingUsername() {
        Account account = createTestAccount();

        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());

        assertThrows(IllegalUpdateException.class, () -> accountService.updateAccount(account));
        verify(accountRepository).findByUsername(USERNAME);
        verifyNoMoreInteractions(accountRepository);
    }

    /**
     * Tests that an illegal update should be thrown if the email does not exist
     */
    @Test
    public void shouldThrowIfIllegalUpdateOnNonExistingEmail() {
        Account account = createTestAccount();

        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(account));
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.empty());

        assertThrows(IllegalUpdateException.class, () -> accountService.updateAccount(account));
        verify(accountRepository).findByUsername(USERNAME);
        verify(accountRepository).findByEmail(EMAIL);
        verifyNoMoreInteractions(accountRepository);
    }

    /**
     * Tests that an account should be retrieved by username
     */
    @Test
    public void shouldGetAccountByUsername() {
        Account account = createTestAccount();

        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(account));

        Account returned = accountService.getAccount(USERNAME);

        assertEquals(account, returned);
        verify(accountRepository).findByUsername(USERNAME);
    }

    /**
     * Tests that an account should be retrieved by username from cache
     */
    @Test
    public void shouldGetAccountByUsernameCached() {
        Account account = createTestAccount();

        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(account));

        accountService.getAccount(USERNAME);
        accountService.getAccount(USERNAME);
        accountService.getAccount(USERNAME);
        accountService.getAccount(USERNAME);

        verify(accountRepository, times(1)).findByUsername(USERNAME);
    }

    /**
     * Tests that null should be returned if a username does not exist
     */
    @Test
    public void shouldReturnNullIfAccountUsernameNotExists() {
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());

        assertNull(accountService.getAccount(USERNAME));
        verify(accountRepository).findByUsername(USERNAME);
    }

    // The tests for getAccount(username, false) are covered by above tests since getAccount(username) is an alias

    /**
     * Tests that an account should be retrieved by email
     */
    @Test
    public void shouldGetAccountByEmail() {
        Account account = createTestAccount();

        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.of(account));

        Account returned = accountService.getAccount(EMAIL, true);

        assertEquals(account, returned);
        verify(accountRepository).findByEmail(EMAIL);
    }

    /**
     * Tests that an account should be retrieved by email
     */
    @Test
    public void shouldGetAccountByEmailCached() {
        Account account = createTestAccount();

        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.of(account));

        accountService.getAccount(EMAIL, true);
        accountService.getAccount(EMAIL, true);
        accountService.getAccount(EMAIL, true);
        accountService.getAccount(EMAIL, true);

        verify(accountRepository, times(1)).findByEmail(EMAIL);
    }

    /**
     * Tests that if an email doesn't exist, null is returned
     */
    @Test
    public void shouldReturnNullIfAccountEmailNotExists() {
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.empty());

        assertNull(accountService.getAccount(EMAIL, true));
        verify(accountRepository).findByEmail(EMAIL);
    }

    /**
     * Tests that UserDetails to be compatible with Spring gets UserDetails correctly
     */
    @Test
    public void shouldGetUserDetails() {
        UserDetails user = new User(USERNAME, ENCRYPTED_PASSWORD, new ArrayList<>());

        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(createTestAccount()));

        UserDetails returned = accountService.loadUserByUsername(USERNAME);

        assertEquals(user, returned);
        verify(accountRepository).findByUsername(USERNAME);
    }

    /**
     * Tests that Spring's UsernameNotFoundException is thrown when username is not found
     */
    @Test
    public void shouldThrowUsernameNotFoundUserDetails() {
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.loadUserByUsername(USERNAME));
        verify(accountRepository).findByUsername(USERNAME);
    }

    /**
     * Tests that a confirmation token should be generated
     */
    @Test
    public void shouldGenerateConfirmationToken() {
        ConfirmationToken token = accountService.generateConfirmationToken(createTestAccount());
        assertEquals(token.getEmail(), EMAIL);
        assertNotNull(token.getToken());
        verify(tokenRepository).save(token);
    }

    /**
     * Tests that an account should be confirmed successfully
     */
    @Test
    public void shouldConfirmAccount() {
        Account account = createTestAccount();
        account.setConfirmed(false);
        ConfirmationToken token = new ConfirmationToken(EMAIL);

        given(tokenRepository.findByEmail(EMAIL))
                .willReturn(Optional.of(token));

        boolean confirmed = accountService.confirmAccount(account, token.getToken());

        assertTrue(confirmed);
        assertTrue(account.isConfirmed());
        verify(accountRepository).save(account);
    }

    /**
     * Tests that confirmation should not take place if there is no token for the account
     */
    @Test
    public void shouldNotConfirmIfNoToken() {
        Account account = createTestAccount();
        account.setConfirmed(false);

        given(tokenRepository.findByEmail(EMAIL))
                .willReturn(Optional.empty());

        boolean confirmed = accountService.confirmAccount(account, new ConfirmationToken(EMAIL).getToken());

        assertFalse(confirmed);
        assertFalse(account.isConfirmed());
        verifyNoInteractions(accountRepository);
    }

    /**
     * Tests that confirmation does not take place if the token does not match
     */
    @Test
    public void shouldNotConfirmIfInvalidToken() {
        Account account = createTestAccount();
        account.setConfirmed(false);
        ConfirmationToken token = new ConfirmationToken(EMAIL);

        given(tokenRepository.findByEmail(EMAIL))
                .willReturn(Optional.of(token));

        boolean confirmed = accountService.confirmAccount(account, new ConfirmationToken(EMAIL).getToken());

        assertFalse(confirmed);
        assertFalse(account.isConfirmed());
        verifyNoInteractions(accountRepository);
    }
}
