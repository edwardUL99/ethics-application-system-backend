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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent", orphanRemoval = true)
    private List<Comment> subComments = new ArrayList<>();
    /**
     * A parent comment if this comment is a sub-comment
     */
    @ManyToOne
    @JsonIgnore
    private Comment parent;
    /**
     * Indicates if the comment is shared (visible) to the applicant (applied to parent and subsequently all sub-comments)
     */
    private boolean sharedApplicant;
    /**
     * Determines if the comment is shared with all reviewers or just admin/chair
     */
    private boolean sharedReviewer;
    /**
     * The time when the comment was created
     */
    private LocalDateTime createdAt;
    /**
     * Determines if the comment has been edited before
     */
    private Boolean edited;

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
        this(id, user, comment, componentId, subComments, createdAt, false, false);
    }

    /**
     * Create a Comment
     * @param id the database ID for the comment
     * @param user the user that made the comment
     * @param comment the comment left
     * @param componentId the ID of the component the comment is attached to
     * @param subComments the sub-comments added to this comment
     * @param createdAt the timestamp the comment was created at
     * @param sharedApplicant indicates if comment can be viewed by applicant (applied to parent and subsequently all sub-comments)
     * @param sharedReviewer determines if the comment has been shared with all reviewers or just chair/committee
     */
    public Comment(Long id, User user, String comment, String componentId, List<Comment> subComments, LocalDateTime createdAt,
                   boolean sharedApplicant, boolean sharedReviewer) {
        this.id = id;
        this.user = user;
        this.comment = comment;
        this.componentId = componentId;
        this.sharedApplicant = sharedApplicant;
        this.sharedReviewer = sharedReviewer;
        subComments.forEach(this::addSubComment);
        this.createdAt = createdAt;
        this.edited = false;
    }

    /**
     * Add a comment to this comment as a sub comment
     * @param comment the comment to add
     */
    public void addSubComment(Comment comment) {
        comment.setComponentId(componentId); // attaches to the same component
        comment.parent = this;
        subComments.add(comment);
    }

    /**
     * Remove the sub comment at the given index
     * @param subComment the sub comment to remove
     */
    public void removeSubComment(Comment subComment) {
        this.subComments.remove(subComment);
        subComment.setParent(null);
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
     * Merge the information from the provided comment into this comment. Must have non-null IDs and be equal
     * @param comment the comment to merge
     */
    public void merge(Comment comment) {
        Long id = comment.getId();

        if (this.id == null || id == null)
            throw new IllegalStateException("Cannot merge unsaved comments");
        else if (!Objects.equals(this.id, id))
            throw new IllegalStateException("Cannot merge different comments: " + this.id + " != " + id);

        componentId = comment.componentId;
        user = comment.user;
        createdAt = comment.createdAt;
        sharedApplicant = comment.sharedApplicant;
        sharedReviewer = comment.sharedReviewer;
        this.comment = comment.comment;
        edited = comment.edited;

        List<Comment> updatedSub = comment.subComments;
        Map<Long, Comment> updatedSubsMap = updatedSub
                .stream()
                .collect(Collectors.toMap(Comment::getId, c -> c));
        List<Comment> toRemove = new ArrayList<>();

        for (Comment subComment : comment.subComments) {
            Comment updatedSubComment = updatedSubsMap.get(subComment.id);

            if (updatedSubComment == null)
                toRemove.add(subComment);
            else
                subComment.merge(updatedSubComment);
        }

        toRemove.forEach(comment::removeSubComment);
    }

    /**
     * Copy this comment and all sub-comments
     * @return the copied comment
     */
    public Comment copy() {
        Comment copied = new Comment(null, user, comment, componentId, new ArrayList<>(), createdAt, sharedApplicant, sharedReviewer);
        copied.edited = edited;
        copied.parent = parent;

        for (Comment sub : subComments)
            copied.addSubComment(sub.copy());

        return copied;
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
                && Objects.equals(componentId, comment.componentId) && Objects.equals(subComments, comment.subComments)
                && Objects.equals(createdAt, comment.createdAt) && Objects.equals(sharedReviewer, comment.sharedReviewer)
                && Objects.equals(sharedApplicant, comment.sharedApplicant) && Objects.equals(edited, comment.edited);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, user, comment, componentId, subComments, createdAt, sharedApplicant, sharedReviewer, edited);
    }
}
