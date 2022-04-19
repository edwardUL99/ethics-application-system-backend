package ie.ul.ethics.scieng.authentication.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

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
    protected String username;
    /**
     * The account's email address
     */
    @Column(unique=true)
    protected String email;
    /**
     * The account's password
     */
    protected String password;
    /**
     * Determines if this account has been confirmed
     */
    protected boolean confirmed;

    /**
     * Create a default Account object
     */
    public Account() {
        this(null, null, null, false);
    }

    /**
     * Create an Account object with the provided attributes
     * @param username the username to associate with the account
     * @param email the email to associate with the account
     * @param password the account's password
     * @param confirmed true if confirmed, false if not
     */
    public Account(String username, String email, String password, boolean confirmed) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmed = confirmed;
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

    /**
     * Determines if this account is confirmed or not
     * @return true if the account is confirmed, false if not
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * Set the value for the account being confirmed
     * @param confirmed the new value for confirmed
     */
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * Determine if object o is equal to this object
     * @param o the object to check equality
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(username, account.username) && Objects.equals(email, account.email) && Objects.equals(password, account.password)
                && Objects.equals(confirmed, account.confirmed);
    }

    /**
     * Generate the hashcode for the provided object
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, email, password, confirmed);
    }
}
