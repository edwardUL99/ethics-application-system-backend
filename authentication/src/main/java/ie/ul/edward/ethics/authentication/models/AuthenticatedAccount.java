package ie.ul.edward.ethics.authentication.models;

import java.time.LocalDateTime;

/**
 * This class represents an AuthenticatedAccount and how long the account remains authenticated for.
 *
 * Information like the user's email address and password is not provided with an instance of this class ({@link Account#getEmail()}
 * and {@link Account#getPassword()} will both return null)
 */
public class AuthenticatedAccount extends Account {
    /**
     * The JWT token used to authenticate this user
     */
    private final String jwtToken;
    /**
     * The date at which the authenticated account is no longer authenticated
     */
    private final LocalDateTime expiration;

    /**
     * Creates an AuthenticatedAccount
     * @param username the username of the account
     * @param jwtToken the token used to authenticate this user
     * @param expiration the date the token expires
     */
    public AuthenticatedAccount(String username, String jwtToken, LocalDateTime expiration) {
        super(username, null, null);
        this.jwtToken = jwtToken;
        this.expiration = expiration;
    }

    /**
     * Retrieve the token used to authenticate this account
     * @return the token used to authenticate the account
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Retrieve the date this account's authorization status expires at
     * @return the expiration date
     */
    public LocalDateTime getExpiration() {
        return expiration;
    }

    /**
     * Checks if this account is no longer authenticated because the token expired
     * @return true if expired, false if still authenticated
     */
    public boolean isExpired() {
        return expiration.isBefore(LocalDateTime.now());
    }
}
