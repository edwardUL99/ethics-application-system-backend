package ie.ul.edward.ethics.authentication.jwt;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * This class contains information about the current authentication
 */
@Getter
@Setter
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
}
