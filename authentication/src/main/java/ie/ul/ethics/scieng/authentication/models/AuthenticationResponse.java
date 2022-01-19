package ie.ul.ethics.scieng.authentication.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * This class represents the response for a successful authentication
 */
@Getter
@Setter
public class AuthenticationResponse {
    /**
     * The username of the authenticated user
     */
    private String username;
    /**
     * The JWT token issued to the user
     */
    private String token;
    /**
     * The expiry timestamp for when the JWT token expires
     */
    private LocalDateTime expiry;

    /**
     * Creates a default authentication response
     */
    public AuthenticationResponse() {
        this(null, null, null);
    }

    /**
     * Creates an authentication response with the provided parameters
     * @param username the username the authentication is for
     * @param token the JWT token issued to the user
     * @param expiry the timestamp it expires at
     */
    public AuthenticationResponse(String username, String token, LocalDateTime expiry) {
        this.username = username;
        this.token = token;
        this.expiry = expiry;
    }
}
