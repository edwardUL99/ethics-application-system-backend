package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.Objects;

/**
 * This class represents an object that contains a user that can view the application. Simply holds a user and a database
 * ID since a list of just Users in application does not appear to work
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserAccess {
    /**
     * The database ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The user given access
     */
    @OneToOne
    private User user;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserAccess that = (UserAccess) o;
        return Objects.equals(user, that.user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(user);
    }
}
