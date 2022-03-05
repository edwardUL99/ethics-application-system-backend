package ie.ul.ethics.scieng.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This class represents a token used for password resets
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordToken {
    /**
     * The username of the user that the token belongs to
     */
    @Id
    private String username;
    /**
     * The token used to identify the reset password request
     */
    private String token;
    /**
     * The expiry timestamp of when the token expires
     */
    private LocalDateTime expiry;

    /**
     * Determines if the token is expired or not
     * @return true if expired, false if not
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ResetPasswordToken that = (ResetPasswordToken) o;
        return Objects.equals(username, that.username) && Objects.equals(token, that.token) && Objects.equals(expiry, that.expiry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(username, token, expiry);
    }
}
