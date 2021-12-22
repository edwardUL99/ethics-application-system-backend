package ie.ul.edward.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The account class provides the "authentication" aspect of a user's account. I.e., rather than containing all the user's
 * information, it only provides the details required for authentication, such as username, email and encrypted password.
 * The other user information is managed by the user management endpoints
 */
@Entity
public class Account {
    /**
     * The account's username
     */
    @Id
    @Column(length=32)
    private String username;
    /**
     * The account's email address
     */
    @Column(unique=true)
    private String email;
    /**
     * The account's password
     */
    private String password;

    /**
     * Create a default Account object
     */
    public Account() {
        this(null, null, null);
    }

    /**
     * Create an Account object with the provided attributes
     * @param username the username to associate with the account
     * @param email the email to associate with the account
     * @param password the account's password
     */
    public Account(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    /**
     * Retrieve the account's username
     * @return the username of the account
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the account's username
     * @param username the new username to use
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieve the account's email address
     * @return the email address associated with this account
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the account's email address
     * @param email the email address for the account
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieve the account's password (may be encrypted if retrieved from the database)
     * @return the account's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password for this account
     * @param password the new password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
