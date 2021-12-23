package ie.ul.edward.ethics.authentication.services;

import ie.ul.edward.ethics.authentication.exceptions.EmailExistsException;
import ie.ul.edward.ethics.authentication.exceptions.IllegalUpdateException;
import ie.ul.edward.ethics.authentication.exceptions.UsernameExistsException;
import ie.ul.edward.ethics.authentication.models.Account;
import ie.ul.edward.ethics.authentication.repositories.AccountRepository;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * This class provides unit tests for the Account Service
 */
@SpringBootTest(properties = {"jwt.secret=ethics-secret-hashing-key-thirty-five-characters-long"}, classes = {ie.ul.edward.ethics.test.utils.TestApplication.class})
public class AccountServiceTest {
    /**
     * The mocked account repository
     */
    @MockBean
    private AccountRepository accountRepository;
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
     * The username for test accounts
     */
    private static final String USERNAME = "user";

    /**
     * The email for test accounts
     */
    private static final String EMAIL = "email@example.com";

    /**
     * The password for test accounts
     */
    private static final String PASSWORD = "user-password";

    /**
     * The encrypted password for testing
     */
    private static final String ENCRYPTED_PASSWORD = "hhsggfsf24552dyuhh&gdhdgg";

    /**
     * Creates an account for testing
     * @return the account for testing
     */
    private Account createTestAccount() {
        return new Account(USERNAME, EMAIL, ENCRYPTED_PASSWORD);
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

        Account account = accountService.createAccount(USERNAME, EMAIL, PASSWORD);

        assertEquals(account.getUsername(), USERNAME);
        assertEquals(account.getEmail(), EMAIL);
        assertEquals(account.getPassword(), ENCRYPTED_PASSWORD);

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

        assertThrows(UsernameExistsException.class, () -> accountService.createAccount(USERNAME, EMAIL, PASSWORD));

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

        assertThrows(EmailExistsException.class, () -> accountService.createAccount(USERNAME, EMAIL, PASSWORD));

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
     * Tests that an account should be updated
     */
    @Test
    public void shouldUpdateAccount() {
        Account account = createTestAccount();

        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.of(account));
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.of(account));

        assertDoesNotThrow(() -> accountService.updateAccount(account));
        verify(accountRepository).findByUsername(USERNAME);
        verify(accountRepository).findByEmail(EMAIL);
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
}
