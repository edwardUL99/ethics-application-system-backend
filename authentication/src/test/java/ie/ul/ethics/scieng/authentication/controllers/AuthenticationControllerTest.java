package ie.ul.ethics.scieng.authentication.controllers;

import ie.ul.ethics.scieng.authentication.exceptions.EmailExistsException;
import ie.ul.ethics.scieng.authentication.exceptions.UsernameExistsException;
import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.authentication.jwt.JWT;
import ie.ul.ethics.scieng.authentication.models.*;
import ie.ul.ethics.scieng.authentication.services.AccountService;
import static ie.ul.ethics.scieng.common.Constants.*;
import static ie.ul.ethics.scieng.authentication.services.AccountServiceTest.*;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ie.ul.ethics.scieng.common.email.EmailSender;
import ie.ul.ethics.scieng.test.utils.JSON;
import ie.ul.ethics.scieng.authentication.test.config.TestConfiguration;
import ie.ul.ethics.scieng.test.utils.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class tests the authentication controller
 */
@SpringBootTest(classes = {
        TestApplication.class,
        TestConfiguration.class
})
public class AuthenticationControllerTest {
    /**
     * Web app context used for testing
     */
    @Autowired
    private WebApplicationContext webApplicationContext;

    /**
     * Used for mocking interactions with the model view controller
     */
    private MockMvc mockMvc;

    /**
     * The account service mock object
     */
    @MockBean
    private AccountService accountService;

    /**
     * The mocked email sender
     */
    @MockBean
    private EmailSender emailSender;

    /**
     * The mock used for JWT
     */
    @MockBean
    private JWT jwt;

    /**
     * The mock used for setting authentication information
     */
    @MockBean
    private AuthenticationInformation authenticationInformation;

    /**
     * A fake jwt token to use for testing
     */
    private static final String JWT_TOKEN = "fake_jwt_token";

    /**
     * The key to use to ensure confirmation always takes place
     */
    private static final String CONFIRMATION_KEY = "always-confirm-ethics-key";

    /**
     * The expiry date for JWT tokens
     */
    private static final LocalDateTime EXPIRY_DATE = LocalDateTime.now().plusHours(2);

    /**
     * Initialises the test mocks
     */
    @BeforeEach
    private void initMocks() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Create a fake authenticated account object for testing
     * @return the fake authenticated account
     */
    private static AuthenticatedAccount createAuthenticatedAccount() {
        return new AuthenticatedAccount(USERNAME, JWT_TOKEN, EXPIRY_DATE);
    }

    /**
     * This method tests that an account should be registered successfully
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldRegisterAccount() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(true);
        RegistrationRequest request = new RegistrationRequest(account);
        request.setConfirmationKey(CONFIRMATION_KEY);
        String json = JSON.convertJSON(request);
        String resultJson = JSON.convertJSON(new AccountResponse(USERNAME, EMAIL, true));

        given(accountService.createAccount(USERNAME, EMAIL, PASSWORD, true))
                .willReturn(account);

        mockMvc.perform(
                post(createApiPath(Endpoint.AUTHENTICATION, "register"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).createAccount(USERNAME, EMAIL, PASSWORD, true);
    }

    /**
     * This method tests that an account should be registered successfully with the account requiring registration
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldRegisterAccountWithRequiredConfirmation() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(false);
        RegistrationRequest request = new RegistrationRequest(account);

        ConfirmationToken token = new ConfirmationToken(EMAIL);

        String json = JSON.convertJSON(request);
        String resultJson = JSON.convertJSON(new AccountResponse(USERNAME, EMAIL));

        given(accountService.createAccount(USERNAME, EMAIL, PASSWORD, false))
                .willReturn(account);
        given(accountService.generateConfirmationToken(account))
                .willReturn(token);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "register"))
                                .contentType(JSON.MEDIA_TYPE)
                                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).createAccount(USERNAME, EMAIL, PASSWORD, false);
    }

    /**
     * This method tests that an error should be returned if the username already exists
     * @throws Exception the exception to throw
     */
    @Test
    public void shouldThrowErrorIfUsernameExists() throws Exception {
        Mockito.doThrow(UsernameExistsException.class).when(accountService).createAccount(USERNAME, EMAIL, PASSWORD, true);

        Account account = createTestAccount();
        RegistrationRequest request = new RegistrationRequest(account);
        request.setConfirmationKey(CONFIRMATION_KEY);
        String json = JSON.convertJSON(request);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, USERNAME_EXISTS);
        String resultJson = JSON.convertJSON(response);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "register"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).createAccount(USERNAME, EMAIL, PASSWORD, true);
    }

    /**
     * This method tests that an error should be returned if the username already exists
     * @throws Exception the exception to throw
     */
    @Test
    public void shouldThrowErrorIfEmailExists() throws Exception {
        Mockito.doThrow(EmailExistsException.class).when(accountService).createAccount(USERNAME, EMAIL, PASSWORD, true);

        Account account = createTestAccount();
        RegistrationRequest request = new RegistrationRequest(account);
        request.setConfirmationKey(CONFIRMATION_KEY);
        String json = JSON.convertJSON(request);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, EMAIL_EXISTS);
        String resultJson = JSON.convertJSON(response);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "register"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).createAccount(USERNAME, EMAIL, PASSWORD, true);
    }

    /**
     * This method tests that a registered account is authenticated successfully
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldAuthenticate() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(true);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(account.getUsername(), account.getPassword(), null, null);
        String json = JSON.convertJSON(authenticationRequest);

        AuthenticatedAccount authenticatedAccount = createAuthenticatedAccount();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(authenticatedAccount.getUsername(), authenticatedAccount.getJwtToken(), authenticatedAccount.getExpiration());
        String resultJson = JSON.convertJSON(authenticationResponse);

        given(accountService.authenticateAccount(account, PASSWORD))
                .willReturn(true);
        given(accountService.getAccount(USERNAME, false))
                .willReturn(account);
        given(jwt.generateToken(account, -1L))
                .willReturn(JWT_TOKEN);
        given(jwt.getAuthenticatedAccount(JWT_TOKEN))
                .willReturn(authenticatedAccount);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "login"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).authenticateAccount(account, PASSWORD);
        verify(accountService).getAccount(USERNAME, false);
    }

    /**
     * This method tests that a registered account is not authenticated if it is not confirmed
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldNotAuthenticateIfNotConfirmed() throws Exception {
        Account account = createTestAccount();
        String password = account.getPassword();

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(account.getUsername(), password, null, null);
        String json = JSON.convertJSON(authenticationRequest);

        Map<String, Object> authenticationResponse = new HashMap<>();
        authenticationResponse.put(ERROR, ACCOUNT_NOT_CONFIRMED);
        String resultJson = JSON.convertJSON(authenticationResponse);

        given(accountService.getAccount(USERNAME, false))
                .willReturn(account);
        given(accountService.authenticateAccount(account, password))
                .willReturn(true);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "login"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).getAccount(USERNAME, false);
        verify(accountService).authenticateAccount(account, password);
    }

    /**
     * This tests that if authentication is requested using email, it will be carried out successfully
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldAuthenticateUsingEmail() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(true);
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(account.getEmail(), account.getPassword(), true, null);
        String json = JSON.convertJSON(authenticationRequest);

        AuthenticatedAccount authenticatedAccount = createAuthenticatedAccount();
        AuthenticationResponse authenticationResponse = new AuthenticationResponse(authenticatedAccount.getUsername(), authenticatedAccount.getJwtToken(), authenticatedAccount.getExpiration());
        String resultJson = JSON.convertJSON(authenticationResponse);

        given(accountService.authenticateAccount(account, PASSWORD))
                .willReturn(true);
        given(accountService.getAccount(EMAIL, true))
                .willReturn(account);
        given(jwt.generateToken(account, -1L))
                .willReturn(JWT_TOKEN);
        given(jwt.getAuthenticatedAccount(JWT_TOKEN))
                .willReturn(authenticatedAccount);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "login"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).authenticateAccount(account, PASSWORD);
        verify(accountService).getAccount(EMAIL, true);
    }

    /**
     * This method tests that expiry should be treated correctly
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldAuthenticateWithExpiry() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(true);

        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(account.getUsername(), account.getPassword(), null, 24L);
        String json = JSON.convertJSON(authenticationRequest);

        LocalDateTime expiry = LocalDateTime.now().plusHours(24L);
        AuthenticatedAccount authenticatedAccount = new AuthenticatedAccount(USERNAME, JWT_TOKEN, expiry);

        AuthenticationResponse authenticationResponse = new AuthenticationResponse(authenticatedAccount.getUsername(),
                authenticatedAccount.getJwtToken(), authenticatedAccount.getExpiration());
        String resultJson = JSON.convertJSON(authenticationResponse);

        given(accountService.authenticateAccount(account, PASSWORD))
                .willReturn(true);
        given(jwt.generateToken(account, 24L))
                .willReturn(JWT_TOKEN);
        given(jwt.getAuthenticatedAccount(JWT_TOKEN))
                .willReturn(authenticatedAccount);
        given(accountService.getAccount(USERNAME, false))
                .willReturn(account);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "login"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(jwt).generateToken(account, 24L); // verify the correct expiry value was passed in
        verify(accountService).authenticateAccount(account, PASSWORD);
        verify(accountService).getAccount(USERNAME, false);
    }

    /**
     * This method tests that an error message should be returned if wrong credentials are entered
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldThrowInvalidCredentialsErrorOnAuthenticate() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(true);
        AuthenticationRequest authenticationRequest =
                new AuthenticationRequest(account.getUsername(), account.getPassword(), null, 24L);
        String json = JSON.convertJSON(authenticationRequest);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_CREDENTIALS);
        String resultJson = JSON.convertJSON(response);

        given(accountService.authenticateAccount(account, PASSWORD))
                .willReturn(false);
        given(accountService.getAccount(USERNAME, false))
                .willReturn(account);

        mockMvc.perform(post(createApiPath(Endpoint.AUTHENTICATION, "login"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).authenticateAccount(account, PASSWORD);
        verify(accountService).getAccount(USERNAME, false);
    }

    /**
     * Tests that an account should be retrieved successfully by username
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldGetAccountByUsername() throws Exception {
        Account account = createTestAccount();
        AccountResponse response = new AccountResponse(account.getUsername(), account.getEmail());
        String resultJson = JSON.convertJSON(response);

        given(accountService.getAccount(USERNAME, false))
                .willReturn(account);

        mockMvc.perform(get(createApiPath(Endpoint.AUTHENTICATION, "account"))
                .param("username", USERNAME)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));
    }

    /**
     * Tests that an account should be retrieved successfully by email
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldGetAccountByEmail() throws Exception {
        Account account = createTestAccount();
        AccountResponse response = new AccountResponse(account.getUsername(), account.getEmail());
        String resultJson = JSON.convertJSON(response);

        given(accountService.getAccount(EMAIL, true))
                .willReturn(account);

        mockMvc.perform(get(createApiPath(Endpoint.AUTHENTICATION, "account"))
                        .param("username", EMAIL)
                        .param("email", "true")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));
    }

    /**
     * Tests that a not found error should be thrown if the user is not found
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldReturnNotFoundOnGetUser() throws Exception {
        given(accountService.getAccount(USERNAME, false))
                .willReturn(null);

        mockMvc.perform(get(createApiPath(Endpoint.AUTHENTICATION, "account"))
                .param("username", USERNAME)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN))
                .andExpect(status().isNotFound());
    }

    /**
     * Tests that an account should be updated successfully
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldUpdateAccount() throws Exception {
        UpdateAccountRequest request = new UpdateAccountRequest(USERNAME, PASSWORD);
        Account account = createTestAccount();
        String json = JSON.convertJSON(request);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(accountService.getAccount(USERNAME))
                .willReturn(account);
        doNothing().when(accountService).updateAccount(account);

        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE, ACCOUNT_UPDATED);
        String resultJson = JSON.convertJSON(response);

        mockMvc.perform(put(createApiPath(Endpoint.AUTHENTICATION, "account"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(authenticationInformation).getUsername();
        verify(accountService).updateAccount(account);
        verify(accountService).getAccount(USERNAME);
    }

    /**
     * This tests that if an update is illegal, the error will be thrown
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldThrowIllegalUpdate() throws Exception {
        UpdateAccountRequest request = new UpdateAccountRequest(USERNAME, PASSWORD);
        String json = JSON.convertJSON(request);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, ILLEGAL_UPDATE);
        String resultJson = JSON.convertJSON(response);

        given(accountService.getAccount(USERNAME))
                .willReturn(null);

        mockMvc.perform(put(createApiPath(Endpoint.AUTHENTICATION, "account"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(authenticationInformation).getUsername();
        verify(accountService).getAccount(USERNAME);
    }

    /**
     * This tests that if a user attempts to update another user's account, a 401 will be raised with a message illegal_update
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldThrowIllegalUpdateIfUpdateAnotherUsersAccount() throws Exception {
        UpdateAccountRequest request = new UpdateAccountRequest("not_my_username", PASSWORD);
        String json = JSON.convertJSON(request);

        Account account = createTestAccount();
        account.setUsername("not_my_username");

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, ILLEGAL_UPDATE);
        String resultJson = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        mockMvc.perform(put(createApiPath(Endpoint.AUTHENTICATION, "account"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(authenticationInformation).getUsername();
        verifyNoInteractions(accountService);
    }

    /**
     * This tests that an admin can update any account without requiring username verification
     * @throws Exception if an error occurs
     */
    @Test
    public void shouldUpdateAccountAdmin() throws Exception {
        UpdateAccountRequest request = new UpdateAccountRequest("not_my_username", PASSWORD);
        String json = JSON.convertJSON(request);

        Account account = createTestAccount();
        account.setUsername("not_my_username");

        // don't need to write tests for Illegal update etc. since updates are the same for admin/non-admin,
        // except for username verification in non-admin requests

        given(accountService.getAccount("not_my_username"))
                .willReturn(account);
        doNothing().when(accountService).updateAccount(account);

        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE, ACCOUNT_UPDATED);
        String resultJson = JSON.convertJSON(response);

        mockMvc.perform(put(createApiPath(Endpoint.AUTHENTICATION, "admin", "account"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + JWT_TOKEN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verifyNoInteractions(authenticationInformation);
        verify(accountService).updateAccount(account);
        verify(accountService).getAccount("not_my_username");
    }

    /**
     * Tests that the controller accepts requests to check if an account is confirmed
     */
    @Test
    public void shouldCheckIfAccountIsConfirmed() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(true);

        given(accountService.getAccount(USERNAME, false))
                .willReturn(account);

        Map<String, Object> response = new HashMap<>();
        response.put("confirmed", true);
        String json = JSON.convertJSON(response);

        mockMvc.perform(get("/api/auth/account/confirmed")
                .param("username", USERNAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(json));

        verify(accountService).getAccount(USERNAME, false);
    }

    /**
     * Tests that the controller accepts requests to check if an account is confirmed and return 404 if account is not
     * found
     */
    @Test
    public void shouldThrowNotFoundOnCheckAccountIsConfirmed() throws Exception {
        Account account = createTestAccount();
        account.setConfirmed(true);

        given(accountService.getAccount(USERNAME, false))
                .willReturn(null);

        mockMvc.perform(get("/api/auth/account/confirmed")
                        .param("username", USERNAME))
                .andExpect(status().isNotFound());

        verify(accountService).getAccount(USERNAME, false);
    }

    /**
     * Tests that the controller accepts requests to confirm an account
     */
    @Test
    public void shouldConfirmAccount() throws Exception {
        Account account = createTestAccount();
        String token = new ConfirmationToken(EMAIL).getToken();

        given(accountService.getAccount(EMAIL, true))
                .willReturn(account);
        given(accountService.confirmAccount(account, token))
                .willReturn(true);

        ConfirmationRequest request = new ConfirmationRequest(EMAIL, token);

        Map<String, Object> response = new HashMap<>();
        response.put("confirmed", true);
        String json = JSON.convertJSON(request);
        String resultJson = JSON.convertJSON(response);

        mockMvc.perform(post("/api/auth/account/confirm")
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).getAccount(EMAIL, true);
        verify(accountService).confirmAccount(account, token);
    }

    /**
     * Tests that the controller accepts requests to confirm an account and returns false if not confirmed
     */
    @Test
    public void shouldNotConfirmAccount() throws Exception {
        Account account = createTestAccount();
        String token = new ConfirmationToken(EMAIL).getToken();

        given(accountService.getAccount(EMAIL, true))
                .willReturn(account);
        given(accountService.confirmAccount(account, token))
                .willReturn(false);

        ConfirmationRequest request = new ConfirmationRequest(EMAIL, token);

        Map<String, Object> response = new HashMap<>();
        response.put("confirmed", false);
        String json = JSON.convertJSON(request);
        String resultJson = JSON.convertJSON(response);

        mockMvc.perform(post("/api/auth/account/confirm")
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(resultJson));

        verify(accountService).getAccount(EMAIL, true);
        verify(accountService).confirmAccount(account, token);
    }

    /**
     * Tests that if an account cannot be found, false should be returned
     */
    @Test
    public void shouldThrowNotFoundOnConfirmAccount() throws Exception {
        String token = new ConfirmationToken(EMAIL).getToken();

        given(accountService.getAccount(EMAIL, true))
                .willReturn(null);

        ConfirmationRequest request = new ConfirmationRequest(EMAIL, token);

        String json = JSON.convertJSON(request);

        mockMvc.perform(post("/api/auth/account/confirm")
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(accountService).getAccount(EMAIL, true);
    }

    /**
     * Tests that a password reset should be requested
     */
    @Test
    public void shouldRequestPasswordReset() throws Exception {
        Account account = createTestAccount();
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken(USERNAME, UUID.randomUUID().toString(), LocalDateTime.now().plusHours(2));

        given(accountService.getAccount(USERNAME, false))
                .willReturn(account);
        given(accountService.requestPasswordReset(account))
                .willReturn(resetPasswordToken);

        mockMvc.perform(post("/api/auth/forgot-password")
                .param("username", USERNAME))
                .andExpect(status().isCreated());

        verify(accountService).getAccount(USERNAME, false);
        verify(accountService).requestPasswordReset(account);
    }

    /**
     * Tests that a 404 should be thrown on a requested password reset if the account doesn't exist
     */
    @Test
    public void shouldThrowNotFoundRequestPasswordReset() throws Exception {
        Account account = createTestAccount();

        given(accountService.getAccount(USERNAME, false))
                .willReturn(null);

        mockMvc.perform(post("/api/auth/forgot-password")
                        .param("username", USERNAME))
                .andExpect(status().isNotFound());

        verifyNoInteractions(emailSender);
        verify(accountService).getAccount(USERNAME, false);
        verify(accountService, times(0)).requestPasswordReset(account);
    }

    /**
     * Tests that a password should be reset successfully
     */
    @Test
    public void shouldResetPassword() throws Exception {
        Account account = createTestAccount();
        String token = UUID.randomUUID().toString();

        given(accountService.getAccount(USERNAME))
                .willReturn(account);
        given(accountService.verifyPasswordResetToken(account, token))
                .willReturn(true);

        ResetPasswordRequest request = new ResetPasswordRequest(USERNAME, token, "new_password");

        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE, ACCOUNT_UPDATED);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        mockMvc.perform(post("/api/auth/reset-password/")
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(accountService).getAccount(USERNAME);
        verify(accountService).verifyPasswordResetToken(account, token);
        verify(accountService).resetPassword(account, "new_password");
    }

    /**
     * Tests that a 404 should be thrown if account not found on reset password
     */
    @Test
    public void shouldThrowNotFoundResetPassword() throws Exception {
        Account account = createTestAccount();
        String token = UUID.randomUUID().toString();

        given(accountService.getAccount(USERNAME))
                .willReturn(null);

        ResetPasswordRequest request = new ResetPasswordRequest(USERNAME, token, "new_password");

        String json = JSON.convertJSON(request);

        mockMvc.perform(post("/api/auth/reset-password/")
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(accountService).getAccount(USERNAME);
        verify(accountService, times(0)).verifyPasswordResetToken(account, token);
        verify(accountService, times(0)).resetPassword(account, "new_password");
    }

    /**
     * Tests that if the token is invalid on password reset, an error will be thrown
     */
    @Test
    public void shouldThrowInvalidTokenResetPassword() throws Exception {
        Account account = createTestAccount();
        String token = UUID.randomUUID().toString();

        given(accountService.getAccount(USERNAME))
                .willReturn(account);
        given(accountService.verifyPasswordResetToken(account, token))
                .willReturn(false);

        ResetPasswordRequest request = new ResetPasswordRequest(USERNAME, token, "new_password");

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_RESET_TOKEN);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        mockMvc.perform(post("/api/auth/reset-password/")
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(accountService).getAccount(USERNAME);
        verify(accountService).verifyPasswordResetToken(account, token);
        verify(accountService, times(0)).resetPassword(account, "new_password");
    }
}
