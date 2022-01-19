package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.users.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a comment left on an application
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Comment {
    /**
     * The comment's ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The user that left the comment
     */
    @OneToOne
    private User user;
    /**
     * The comment text
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String comment;
    /**
     * The ID of the component the comment is attached to
     */
    private String componentId;
    /**
     * The list of subComments of this comment
     */
    @OneToMany(cascade = CascadeType.ALL)
    private List<Comment> subComments = new ArrayList<>();

    /**
     * Create a Comment
     * @param id the database ID for the comment
     * @param user the user that made the comment
     * @param comment the comment left
     * @param componentId the ID of the component the comment is attached to
     * @param subComments the sub-comments added to this comment
     */
    public Comment(Long id, User user, String comment, String componentId, List<Comment> subComments) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.componentId = componentId;
        subComments.forEach(this::addSubComment);
    }

    /**
     * Add a comment to this comment as a sub comment
     * @param comment the comment to add
     */
    public void addSubComment(Comment comment) {
        comment.setComponentId(componentId); // attaches to the same component
        subComments.add(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return id != null && Objects.equals(id, comment.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
