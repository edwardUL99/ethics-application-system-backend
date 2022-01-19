package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.models.authorization.Permission;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents an application that has been submitted
 */
@Entity
@Getter
@Setter
public class SubmittedApplication extends Application {
    /**
     * The mapping of componentIDs to comments
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "comments_mapping",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "comment_id", referencedColumnName = "id")})
    @MapKey(name = "componentId")
    protected Map<String, Comment> comments;
    /**
     * The list of assigned committee members
     */
    @OneToMany
    protected List<User> assignedCommitteeMembers = new ArrayList<>();
    /**
     * This comment is the final comment given to the application (i.e. when approved or rejected). Is not set through a
     * constructor but can be set through {@link #setFinalComment(Comment)}
     */
    @OneToOne(cascade = CascadeType.ALL)
    protected Comment finalComment;

    /**
     * Create a default Application
     */
    public SubmittedApplication() {
        super();
    }

    /**
     * Create an Application
     *
     * @param id                  the database ID of the application
     * @param applicationId       the ethics committee application ID
     * @param user                the user that owns the application
     * @param status              the status of the application
     * @param applicationTemplate the template that this application was answered on
     * @param answers              the answers to the application
     * @param comments            the list of comments on this application
     * @param assignedCommitteeMembers the list of assigned committee members
     */
    public SubmittedApplication(Long id, String applicationId, User user, ApplicationStatus status,
                                ApplicationTemplate applicationTemplate, Map<String, Answer> answers,
                                List<Comment> comments, List<User> assignedCommitteeMembers) {
        super(id, applicationId, user, status, applicationTemplate, answers);
        this.comments = comments.stream()
                .collect(Collectors.toMap(
                        Comment::getComponentId,
                        c -> c
                ));
        assignedCommitteeMembers.forEach(this::assignCommitteeMember);
    }

    /**
     * Assign committee member to the application
     * @param user the committee member to assign
     * @throws ApplicationException if the user does not have REVIEW_APPLICATIONS permissions
     */
    public void assignCommitteeMember(User user) {
        if (!user.getRole().getPermissions().contains(Permissions.REVIEW_APPLICATIONS))
            throw new ApplicationException("The user being assigned to the SubmittedApplication must have the REVIEW_APPLICATIONS permission");

        assignedCommitteeMembers.add(user);
    }

    /**
     * This method determines if the provided user can view this application
     *
     * @param user the user that wishes to view the application
     * @return true if they can view it, false if not
     */
    @Override
    public boolean canBeViewedBy(User user) {
        Collection<Permission> permissions = user.getRole().getPermissions();

        return (this.user.getUsername().equals(user.getUsername()) && permissions.contains(Permissions.VIEW_OWN_APPLICATIONS))
                || permissions.contains(Permissions.REVIEW_APPLICATIONS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SubmittedApplication that = (SubmittedApplication) o;
        return id != null && Objects.equals(id, that.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}