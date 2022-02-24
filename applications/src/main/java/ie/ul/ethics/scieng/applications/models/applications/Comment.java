package ie.ul.ethics.scieng.applications.models.applications;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ie.ul.ethics.scieng.users.models.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    @JsonIgnore
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
     * The time when the comment was created
     */
    private LocalDateTime createdAt;

    /**
     * Create a Comment
     * @param id the database ID for the comment
     * @param user the user that made the comment
     * @param comment the comment left
     * @param componentId the ID of the component the comment is attached to
     * @param subComments the sub-comments added to this comment
     */
    public Comment(Long id, User user, String comment, String componentId, List<Comment> subComments) {
        this(id, user, comment, componentId, subComments, LocalDateTime.now());
    }

    /**
     * Create a Comment
     * @param id the database ID for the comment
     * @param user the user that made the comment
     * @param comment the comment left
     * @param componentId the ID of the component the comment is attached to
     * @param subComments the sub-comments added to this comment
     * @param createdAt the timestamp the comment was created at
     */
    public Comment(Long id, User user, String comment, String componentId, List<Comment> subComments, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.componentId = componentId;
        subComments.forEach(this::addSubComment);
        this.createdAt = createdAt;
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
     * Return the username of the commenter
     * @return commenter's username
     */
    @JsonProperty("username")
    public String getCommenterUsername() {
        return (user == null) ? null:user.getUsername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(user, comment.user) && Objects.equals(this.comment, comment.comment)
                && Objects.equals(componentId, comment.componentId) && Objects.equals(subComments, comment.subComments) && Objects.equals(createdAt, comment.createdAt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, user, comment, componentId, subComments, createdAt);
    }
}
