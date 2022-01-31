package ie.ul.ethics.scieng.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a response containing a "clean" account, i.e. an account that does not return the password
 */
@Getter
@Setter
@AllArgsConstructor
public class AccountResponse {
    /**
     * The username for the account
     */
    protected String username;
    /**
     * The email for the account
     */
    protected String email;
    /**
     * The value for if the response is confirmed or not
     */
    protected boolean confirmed = false;

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
