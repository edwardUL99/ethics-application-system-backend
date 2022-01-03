package ie.ul.edward.ethics.authentication.controllers;

import ie.ul.edward.ethics.authentication.exceptions.EmailExistsException;
import ie.ul.edward.ethics.authentication.exceptions.IllegalUpdateException;
import ie.ul.edward.ethics.authentication.exceptions.UsernameExistsException;
import ie.ul.edward.ethics.authentication.jwt.JWT;
import ie.ul.edward.ethics.authentication.models.*;
import ie.ul.edward.ethics.authentication.services.AccountService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
     * Create an AuthenticationController
     * @param authenticationManager authentication manager for authenticating requests
     * @param jwt utilities for JWT token authentication
     * @param accountService the service used for creating and retrieving accounts
     */
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JWT jwt, AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.accountService = accountService;
    }

    /**
     * This endpoint provides the registration endpoint
     * @param account the account to register
     * @return the JSON response
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid Account account) {
        Map<String, Object> response = new HashMap<>();

        try {
            String username = account.getUsername();
            String email = account.getEmail();

            Account createdAccount = accountService.createAccount(username, email, account.getPassword());

            return ResponseEntity.status(HttpStatus.CREATED).body(new AccountResponse(createdAccount.getUsername(), createdAccount.getEmail()));
        } catch (UsernameExistsException ex) {
            log.error(ex);
            response.put(ERROR, USERNAME_EXISTS);
        } catch (EmailExistsException ex) {
            log.error(ex);
            response.put(ERROR, EMAIL_EXISTS);
        }

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Looks up the account with username as email if useEmail is true.
     * Otherwise, returns an account with username and all other fields null.
     * @param username the username/email
     * @param useEmail true if username is an email and to lookup the true username, else just use it as username
     * @return the created account
     */
    private Account accountLookup(String username, boolean useEmail) {
        if (useEmail)
            return accountService.getAccount(username, true);
        else
            return new Account(username, null, null);
    }

    /**
     * The endpoint for authentication
     * @param request the authentication request object
     * @return the response of the request
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody @Valid  AuthenticationRequest request) {
        Map<String, Object> response = new HashMap<>();

        String username = request.getUsername();
        String password = request.getPassword();

        Account account = accountLookup(username, request.isEmail());

        if (account == null) {
            response.put(ERROR, INVALID_CREDENTIALS);
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
                response.put(ERROR, errorMsg);
            }
        }

        return ResponseEntity.badRequest().body(response);
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
     * This method updates the given account
     * @param updated the account to update
     * @return the updated account or an error if it occurs
     */
    @PutMapping("/account")
    public ResponseEntity<?> updateAccount(@RequestBody @Valid Account updated) {
        Map<String, Object> response = new HashMap<>();

        try {
            accountService.updateAccount(updated);

            response.put(MESSAGE, ACCOUNT_UPDATED);

            return ResponseEntity.ok(response);
        } catch (IllegalUpdateException ex) {
            ex.printStackTrace();
            response.put(ERROR, ILLEGAL_UPDATE);
        }

        return ResponseEntity.badRequest().body(response);
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
}
