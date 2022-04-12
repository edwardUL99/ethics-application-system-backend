package ie.ul.ethics.scieng.applications.models.applications.answerrequest;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.users.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a request to a supervisor/other user to fill in a field on an applicant's application,
 * e.g. a signature
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AnswerRequest {
    /**
     * The database ID of the object
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The application associated with the edit
     */
    @OneToOne
    private Application application;
    /**
     * The user requested to answer
     */
    @OneToOne
    private User user;
    /**
     * The list of components that have requested an edit
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<ApplicationComponent> components;
    /**
     * The timestamp of then the request was made
     */
    private LocalDateTime requestedAt;

    /**
     * Check if this object is equal to the provided one
     * @param o the object to check equality
     * @return true if equal, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AnswerRequest that = (AnswerRequest) o;
        return Objects.equals(id, that.id) && Objects.equals(application, that.application) && Objects.equals(user, that.user)
                && Objects.equals(components, that.components);
    }

    /**
     * Generate a hashcode for this object
     * @return the generated hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, application, user, components);
    }
}
