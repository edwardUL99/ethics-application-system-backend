package ie.ul.edward.ethics.authentication.controllers;

import ie.ul.edward.ethics.authentication.exceptions.EmailExistsException;
import ie.ul.edward.ethics.authentication.exceptions.UsernameExistsException;
import ie.ul.edward.ethics.authentication.jwt.JwtAuthentication;
import ie.ul.edward.ethics.authentication.models.Account;
import ie.ul.edward.ethics.authentication.services.AccountService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
    private final JwtAuthentication jwtAuthentication;
    /**
     * The account service used for creating and retrieving accounts
     */
    private final AccountService accountService;

    /**
     * Create an AuthenticationController
     * @param authenticationManager authentication manager for authenticating requests
     * @param jwtAuthentication utilities for JWT token authentication
     * @param accountService the service used for creating and retrieving accounts
     */
    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, JwtAuthentication jwtAuthentication, AccountService accountService) {
        this.authenticationManager = authenticationManager;
        this.jwtAuthentication = jwtAuthentication;
        this.accountService = accountService;
    }

    /**
     * This endpoint provides the registration endpoint
     * @param account the account to register
     * @return the JSON response
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        Map<String, Object> response = new HashMap<>();

        try {
            // TODO may need validation
            Account createdAccount = accountService.createAccount(account.getUsername(), account.getEmail(), account.getPassword());
            createdAccount.setPassword(""); // hide the password from the response

            return ResponseEntity.ok(createdAccount);
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
     * The endpoint for authentication
     * @param account the account sent for authentication
     * @return the response of the request
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody Account account) {
        Map<String, Object> response = new HashMap<>();

        AtomicReference<String> error = new AtomicReference<>();
        authenticateInternal(account.getUsername(), account.getPassword(), error);
        String errorMsg = error.get();

        if (errorMsg == null) {
            String token = jwtAuthentication.generateToken(account);
            return ResponseEntity.ok(jwtAuthentication.getAuthenticatedAccount(token));
        } else {
            response.put(ERROR, errorMsg);
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
