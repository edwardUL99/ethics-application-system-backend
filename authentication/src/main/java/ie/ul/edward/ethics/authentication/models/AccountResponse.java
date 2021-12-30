package ie.ul.edward.ethics.authentication.models;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a response containing a "clean" account, i.e. an account that does not return the password
 */
@Getter
@Setter
public class AccountResponse {
    /**
     * The username for the account
     */
    private String username;
    /**
     * The email for the account
     */
    private String email;

    /**
     * Create a default account response
     */
    public AccountResponse() {
        this(null, null);
    }

    /**
     * Create an AccountResponse with the provided username and email
     * @param username the username for the account
     * @param email the email for the account
     */
    public AccountResponse(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
