package ie.ul.edward.ethics.authentication.models;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a successful response to a registration request
 */
@Getter
@Setter
public class RegistrationResponse extends AccountResponse {
    /**
     * The token to use for confirmation
     */
    private String confirmationToken;

    /**
     * The token to use when confirmation has been automatically done
     */
    public static final String CONFIRMED_TOKEN = "CONFIRMED";

    /**
     * Create a default registration response
     */
    public RegistrationResponse() {
        this(null, null, null);
    }

    /**
     * Create an AccountResponse with the provided username and email
     * @param username the username for the account
     * @param email the email for the account
     * @param confirmationToken the token to use for confirmation
     */
    public RegistrationResponse(String username, String email, String confirmationToken) {
        this.username = username;
        this.email = email;
        this.confirmationToken = confirmationToken;
    }
}
