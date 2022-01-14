package ie.ul.edward.ethics.authentication.controllers;

import ie.ul.edward.ethics.authentication.config.AuthenticationConfiguration;
import ie.ul.edward.ethics.authentication.exceptions.EmailExistsException;
import ie.ul.edward.ethics.authentication.exceptions.IllegalUpdateException;
import ie.ul.edward.ethics.authentication.exceptions.UsernameExistsException;
import ie.ul.edward.ethics.authentication.jwt.AuthenticationInformation;
import ie.ul.edward.ethics.authentication.jwt.JWT;
import ie.ul.edward.ethics.authentication.models.*;
import ie.ul.edward.ethics.authentication.services.AccountService;
import ie.ul.edward.ethics.common.email.EmailSender;
import ie.ul.edward.ethics.common.email.exceptions.EmailException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static ie.ul.edward.ethics.common.Constants.*;

/**
 * This class represents the endpoint for authentication
 */
@RestController
@CrossOrigin
@RequestMapping("/api/auth")
@Log4j2
public class AuthenticationController {
    /**
     * The authentication manager for authenticating requests
     */
    private final AuthenticationManager authenticationManager;
    /**
     * The authentication utilities for JWT tokens
     */
    private final JWT jwt;
    /**
     * The account service used for creating and retrieving accounts
     */
    private final AccountService accountService;
    /**
     * The authentication information object
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;
    /**
     * The configuration object for the authentication module
     */
    private final AuthenticationConfiguration authenticationConfiguration;
    /**
     * The email sender implementation for sending emails
     */
    private final EmailSender emailSender;

    /**
     * Create an AuthenticationController
     * @param authenticationManager authentication manager for authenticating requests
     * @param jwt utilities for JWT token authentication
     * @param accountService the service used for creating and retrieving accounts
     * @param authenticationConfiguration the configuration for the authentication module
     * @param emailSender the email sender implementation for sending emails
     */
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JWT jwt, AccountService accountService,
                                    AuthenticationConfiguration authenticationConfiguration, EmailSender emailSender) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.accountService = accountService;
        this.authenticationConfiguration = authenticationConfiguration;

        if (authenticationConfiguration.isAlwaysConfirm())
            log.warn("The system is configured to automatically confirm any new account. This is dangerous and should only be used for testing");

        this.emailSender = emailSender;
    }

    /**
     * Checks if the account should be confirmed straight away
     * @param confirmationKey the confirmation key from the request
     * @return the response of this registration request
     */
    private boolean alwaysConfirm(String confirmationKey) {
        boolean alwaysConfirm = authenticationConfiguration.isAlwaysConfirm();
        boolean keyMatch = authenticationConfiguration.getConfirmationKey().equals(confirmationKey);

        if (!alwaysConfirm && keyMatch)
            log.warn("Registration request contained a valid confirmation key, so account will be confirmed automatically");

        return alwaysConfirm || keyMatch;
    }

    /**
     * Send the confirmation email to the email specified in the account
     * @param account the account to sent the email to
     * @param confirmationToken the token for confirmation
     */
    private void sendConfirmationEmail(Account account, ConfirmationToken confirmationToken) {
        // TODO update the email with correct links and styling etc
        String content = "<h2>Confirm Account</h2>"
                + "<p>Hello %s,<br>We have received a registration request for an account. You will need to confirm" +
                " the email address before we can proceed with registration</p>"
                + "<br>"
                + "<p>Your username is: <b>%s</b></p>"
                + "<p>Follow this link to confirm your account: <a href=\"%s\">Reset Password</a></p>"
                + "<br>"
                + "<p>If for some reason, the link doesn't work, go to the <a href=\"http://localhost:8080/ethics-app/confirm-account\">confirm account</a> page"
                + " and enter the following details in the first 2 fields:</p>"
                + "<ul>"
                + "<li><b>E-mail:</b> %s</li>"
                + "<li><b>Confirmation Token:</b> %s</li>"
                + "</ul>"
                + "<p><b>Do not</b> give this token (or above link) to anybody else</p>"
                + "<br>"
                + "<p>If you did not request an account, you can safely ignore this e-mail</p>"
                + "<br>"
                + "<p>Thank You,<p>"
                + "<p>The Team</p>";

        String username = account.getUsername();
        String email = account.getEmail();
        String token = confirmationToken.getToken();
        content = String.format(content, username, username,
                String.format("http://localhost:8080/ethics-app/confirm-account?email=%s&token=%s", email, token),
                email, token);

        emailSender.sendEmail(email, "Confirm Account Registration", content);
    }

    /**
     * This endpoint provides the registration endpoint
     * @param request the registration request
     * @return the JSON response
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegistrationRequest request) {
        Account createdAccount = null;
        try {
            String username = request.getUsername();
            String email = request.getEmail();

            createdAccount = accountService.createAccount(username, email, request.getPassword(), alwaysConfirm(request.getConfirmationKey()));

            AccountResponse response;
            if (createdAccount.isConfirmed()) {
                response = new AccountResponse(username, email);
            } else {
                ConfirmationToken token = accountService.generateConfirmationToken(createdAccount);
                sendConfirmationEmail(createdAccount, token);
                response = new AccountResponse(username, email);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UsernameExistsException ex) {
            log.error(ex);
            return respondError(USERNAME_EXISTS);
        } catch (EmailExistsException ex) {
            log.error(ex);
            return respondError(EMAIL_EXISTS);
        } catch (EmailException ex) {
            log.error(ex);

            if (createdAccount != null)
                accountService.deleteAccount(createdAccount);

            return respondError(REGISTRATION_FAILED);
        }
    }

    /**
     * The endpoint for authentication
     * @param request the authentication request object
     * @return the response of the request
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid  AuthenticationRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();

        Account account = accountService.getAccount(username, request.isEmail());

        if (account == null) {
            return respondError(INVALID_CREDENTIALS);
        } else if (!account.isConfirmed()) {
            return respondError(ACCOUNT_NOT_CONFIRMED);
        } else {
            username = account.getUsername();

            AtomicReference<String> error = new AtomicReference<>();
            authenticateInternal(username, password, error);
            String errorMsg = error.get();

            if (errorMsg == null) {
                String token = jwt.generateToken(account, request.getExpiry());
                AuthenticatedAccount authenticatedAccount = (AuthenticatedAccount) jwt.getAuthenticatedAccount(token);
                return ResponseEntity.ok(new AuthenticationResponse(authenticatedAccount.getUsername(),
                        authenticatedAccount.getJwtToken(), authenticatedAccount.getExpiration()));
            } else {
                return respondError(errorMsg);
            }
        }
    }

    /**
     * This method retrieves the user with the given username or email
     * @param username the username or email if email param is true
     * @param email true if username is email, else treat username as username
     * @return the response
     */
    @GetMapping("/account")
    public ResponseEntity<?> getAccount(@RequestParam String username, @RequestParam(required = false) boolean email) {
        Account account = accountService.getAccount(username, email);

        return (account == null) ? ResponseEntity.notFound().build():ResponseEntity.ok(new AccountResponse(account.getUsername(), account.getEmail()));
    }

    /**
     * Performs the account update. This method assumes that authentication has been performed.
     * Authentication should be done by the controller endpoints
     * @param request the request to update
     * @return the response entity associated with the account
     */
    private ResponseEntity<?> updateAccountInternal(UpdateAccountRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = request.getUsername();

            Account account = accountService.getAccount(username);

            if (account == null)
                throw new IllegalUpdateException(username, false);

            account.setPassword(request.getPassword());

            accountService.updateAccount(account);

            response.put(MESSAGE, ACCOUNT_UPDATED);

            return ResponseEntity.ok(response);
        } catch (IllegalUpdateException ex) {
            ex.printStackTrace();
            return respondError(ILLEGAL_UPDATE);
        }
    }

    /**
     * This method updates the given account. The account must belong to the username that has been authenticated
     * @param updated the account to update
     * @return the updated account or an error if it occurs
     */
    @PutMapping("/account")
    public ResponseEntity<?> updateAccount(@RequestBody @Valid UpdateAccountRequest updated) {
        Map<String, Object> response = new HashMap<>();

        String authenticatedUsername = authenticationInformation.getUsername();

        if (authenticatedUsername == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (!authenticatedUsername.equals(updated.getUsername())) {
            response.put(ERROR, ILLEGAL_UPDATE);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } else {
            return updateAccountInternal(updated);
        }
    }

    /**
     * This endpoint is used by admins to update any account.
     * It should be locked to users with only the ADMIN permission, see the users module for permissions authorization
     * @param account the account to update
     * @return the response body
     */
    @PutMapping("/admin/account")
    public ResponseEntity<?> updateAccountAdmin(@RequestBody @Valid UpdateAccountRequest account) {
        return updateAccountInternal(account);
    }

    /**
     * Authenticates the username and password with the authentication manager
     * @param username the username to authenticate with
     * @param password the password to authenticate with
     * @param error if this is not null, an error occurred
     */
    private void authenticateInternal(String username, String password, AtomicReference<String> error) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException ex) {
            log.error(ex);
            error.set(USER_DISABLED);
        } catch (BadCredentialsException ex) {
            log.error(ex);
            error.set(INVALID_CREDENTIALS);
        }
    }

    /**
     * This endpoint checks if an account is confirmed
     * @param username the username of the account. Can be an email
     * @param email true if the username is an email, false if not
     * @return response body
     */
    @GetMapping("/account/confirmed")
    public ResponseEntity<?> isConfirmed(@RequestParam String username, @RequestParam(required = false) boolean email) {
        Account account = accountService.getAccount(username, email);

        if (account == null) {
            return ResponseEntity.notFound().build();
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("confirmed", account.isConfirmed());

            return ResponseEntity.ok(response);
        }
    }

    /**
     * This endpoint confirms the account if it is a valid confirmation request
     * @param request the request to confirm the account
     * @return the response body
     */
    @PostMapping("/account/confirm")
    public ResponseEntity<?> confirmAccount(@RequestBody @Valid ConfirmationRequest request) {
        String email = request.getEmail();
        String token = request.getToken();

        Account account = accountService.getAccount(email, true);

        if (account == null) {
            return ResponseEntity.notFound().build();
        } else {
            boolean confirmed = accountService.confirmAccount(account, token);

            Map<String, Object> response = new HashMap<>();
            response.put("confirmed", confirmed);

            return ResponseEntity.ok(response);
        }
    }
}
