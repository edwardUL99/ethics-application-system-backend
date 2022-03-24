package ie.ul.ethics.scieng.applications.models.applications;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

/**
 * This class represents comments left on an application component
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class ApplicationComments {
    /**
     * The database ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The ID of the component the comments are left on
     */
    private String componentId;
    /**
     * The list of comments on the component
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> comments;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationComments that = (ApplicationComments) o;
        return Objects.equals(id, that.id) && Objects.equals(componentId, that.componentId) && Objects.equals(comments, that.comments);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, componentId, comments);
    }
}
