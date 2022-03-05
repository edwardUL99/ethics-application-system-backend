package ie.ul.ethics.scieng.authentication.models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * This class represents a token used to confirm an account
 */
@Entity
public class ConfirmationToken {
    /**
     * Use the email as the key since we are using email for confirmation
     */
    @Id
    private String email;
    /**
     * The generated confirmation token
     */
    private String token;
    /**
     * The timestamp of when the confirmation token was created
     */
    private LocalDateTime timeCreated;

    /**
     * Create a default ConfirmationToken
     */
    public ConfirmationToken() {
        this(null, null);
    }

    /**
     * Create a random confirmation token for the provided email
     * @param email the email to confirm
     */
    public ConfirmationToken(String email) {
        this(email, UUID.randomUUID().toString());
    }

    /**
     * Create a ConfirmationToken with the provided email and token
     * @param email the email this token is for
     * @param token the token to use for confirmation
     */
    public ConfirmationToken(String email, String token) {
        this.email = email;
        this.token = token;
        this.timeCreated = LocalDateTime.now();
    }

    /**
     * Retrieve the email that this token is confirming
     * @return the email used for confirmation
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email for this token
     * @param email the new email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Retrieve the token used for confirmation
     * @return confirmation token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set the token sed for confirmation
     * @param token the new token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Get the time the confirmation token was created
     * @return time confirmation token was created
     */
    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    /**
     * Set the creation time of the confirmation token
     * @param timeCreated the time the confirmation token was created
     */
    public void setTimeCreated(LocalDateTime timeCreated) {
        this.timeCreated = timeCreated;
    }

    /**
     * Check if this object is equal to the provided object
     * @param o the object to check equality
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmationToken that = (ConfirmationToken) o;
        return Objects.equals(email, that.email) && Objects.equals(token, that.token) && Objects.equals(timeCreated, that.timeCreated);
    }

    /**
     * Generate a hash code for this object
     * @return the generated hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(email, token, timeCreated);
    }
}
