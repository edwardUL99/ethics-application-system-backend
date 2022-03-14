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
 * This class represents a committee member that is assigned to the application. It holds the user that is assigned
 * and the status of whether they are assigned or not
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class AssignedCommitteeMember {
    /**
     * The database ID of this object
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The ID of the application the member is assigned to
     */
    private String applicationId;
    /**
     * The committee member that is assigned
     */
    @OneToOne
    private User user;
    /**
     * Determine if the committee member has finished their review
     */
    private boolean finishReview;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AssignedCommitteeMember that = (AssignedCommitteeMember) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(user, that.user) && Objects.equals(finishReview, that.finishReview);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, user, finishReview);
    }
}
