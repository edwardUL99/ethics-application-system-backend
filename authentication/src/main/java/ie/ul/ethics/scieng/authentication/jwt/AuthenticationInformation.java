package ie.ul.ethics.scieng.authentication.jwt;

import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * This class contains information about the current authentication
 */
@EqualsAndHashCode
public class AuthenticationInformation {
    /**
     * The token used for authentication
     */
    private String token;
    /**
     * The username of the authenticated user
     */
    private String username;
    /**
     * The timestamp when the provided token expires
     */
    private LocalDateTime expiry;

    /**
     * Creates a default AuthenticationInformation object
     */
    public AuthenticationInformation() {
        this(null, null, null);
    }

    /**
     * The authenticated information
     * @param token the token used for authentication
     * @param username the username used for authentication
     * @param expiry the expiry date of the token
     */
    public AuthenticationInformation(String token, String username, LocalDateTime expiry) {
        this.token = token;
        this.username = username;
        this.expiry = expiry;
    }

    /**
     * Get the token used to authenticate the request
     * @return token used to authenticate the request
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the token used to authenticate the request
     * @param token the token used to authenticate the request
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get the username used to authenticate the result
     * @return the username of the authenticated account
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username used to authenticate the request
     * @param username the username of the authenticated user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the timestamp of when the authenticated request is due to expire
     * @return the expiry timestamp
     */
    public LocalDateTime getExpiry() {
        return expiry;
    }

    /**
     * Set the timestamp for when the authenticated request should expire
     * @param expiry the new expiry timestamp
     */
    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }
}
