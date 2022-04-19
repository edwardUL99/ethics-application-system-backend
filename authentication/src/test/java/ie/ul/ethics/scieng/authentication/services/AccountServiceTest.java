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
import ie.ul.ethics.scieng.authentication.test.config.TestConfiguration;
import ie.ul.ethics.scieng.test.utils.TestApplication;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import static org.mockito.Mockito.*;

/**
 * This class provides unit tests for the Account Service
 */
@SpringBootTest(classes = {
        TestApplication.class,
        TestConfiguration.class
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
     * The reset token repository mock for testing
     */
    @MockBean
    private ResetPasswordTokenRepository resetTokenRespository;
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
        Account test = createTestAccount();
        test.setPassword(ENCRYPTED_PASSWORD);

        given(passwordEncoder.encode(PASSWORD))
                .willReturn(ENCRYPTED_PASSWORD);
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.empty());
        given(accountRepository.save(test))
                .willReturn(test);

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
        Account test = createTestAccount();
        test.setConfirmed(true);
        test.setPassword(ENCRYPTED_PASSWORD);

        given(passwordEncoder.encode(PASSWORD))
                .willReturn(ENCRYPTED_PASSWORD);
        given(accountRepository.findByUsername(USERNAME))
                .willReturn(Optional.empty());
        given(accountRepository.findByEmail(EMAIL))
                .willReturn(Optional.empty());
        given(accountRepository.save(test))
                .willReturn(test);

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

    /**
     * Tests that a password reset should be requested
     */
    @Test
    public void shouldRequestPasswordReset() {
        Account account = createTestAccount();
        ResetPasswordToken token = new ResetPasswordToken(USERNAME, UUID.randomUUID().toString(), LocalDateTime.now().plusHours(2));

        ResetPasswordToken returned = accountService.requestPasswordReset(account);

        assertNotNull(returned);
        assertEquals(token.getUsername(), returned.getUsername());
        assertNotNull(token.getToken());
        assertNotNull(token.getExpiry());
        verify(resetTokenRespository).save(any(ResetPasswordToken.class));
    }

    /**
     * Tests that a reset password token should be verified successfully
     */
    @Test
    public void shouldVerifyResetPasswordToken() {
        Account account = createTestAccount();
        String uuid = UUID.randomUUID().toString();
        ResetPasswordToken token = new ResetPasswordToken(USERNAME, uuid, LocalDateTime.now().plusHours(2));

        given(resetTokenRespository.findByUsername(USERNAME))
                .willReturn(Optional.of(token));

        boolean verified = accountService.verifyPasswordResetToken(account, uuid);

        assertTrue(verified);

        // test that if no token exists, verification fails
        given(resetTokenRespository.findByUsername(USERNAME))
                .willReturn(Optional.empty());

        verified = accountService.verifyPasswordResetToken(account, uuid);
        assertFalse(verified);

        // test that an expired token should fail
        ResetPasswordToken expired = new ResetPasswordToken(USERNAME, uuid, LocalDateTime.now().minusHours(2));

        given(resetTokenRespository.findByUsername(USERNAME))
                .willReturn(Optional.of(expired));

        verified = accountService.verifyPasswordResetToken(account, uuid);
        assertFalse(verified);

        // test that if the token differs from provided token, don't verify
        token.setToken(uuid + "-1");

        given(resetTokenRespository.findByUsername(USERNAME))
                .willReturn(Optional.of(token));

        verified = accountService.verifyPasswordResetToken(account, uuid);
        assertFalse(verified);

        verify(resetTokenRespository, times(4)).findByUsername(USERNAME);
    }

    /**
     * Tests that a password should be reset successfully
     */
    @Test
    public void shouldResetPassword() {
        Account account = createTestAccount();
        String newPassword = "new_password";
        String encrypted = "encrypted";

        given(passwordEncoder.encode(newPassword))
                .willReturn(encrypted);

        accountService.resetPassword(account, newPassword);

        assertEquals(encrypted, account.getPassword());
        verify(resetTokenRespository).deleteById(USERNAME);
        verify(accountRepository).save(account);
    }
}
