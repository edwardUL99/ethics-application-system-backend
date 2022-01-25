package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.models.authorization.Permission;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

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
    @Getter(AccessLevel.NONE)
    protected List<User> assignedCommitteeMembers = new ArrayList<>();
    /**
     * This comment is the final comment given to the application (i.e. when approved or rejected). Is not set through a
     * constructor but can be set through {@link #setFinalComment(Comment)}
     */
    @OneToOne(cascade = CascadeType.ALL)
    protected Comment finalComment;
    /**
     * A list of committee members that were assigned to the application before it was referred and re-submitted
     */
    @OneToMany
    @Getter(AccessLevel.NONE)
    protected List<User> previousCommitteeMembers = new ArrayList<>();

    /**
     * Create a default Application
     */
    public SubmittedApplication() {
        this(null, null, null, null, null, new HashMap<>(), new ArrayList<>(), new ArrayList<>(), null);
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
     * @param finalComment        the final comment given to the application if approved/rejected
     */
    public SubmittedApplication(Long id, String applicationId, User user, ApplicationStatus status,
                                ApplicationTemplate applicationTemplate, Map<String, Answer> answers,
                                List<Comment> comments, List<User> assignedCommitteeMembers, Comment finalComment) {
        super(id, applicationId, user, status, applicationTemplate, answers);
        this.comments = new HashMap<>();
        comments.forEach(this::addComment);
        assignedCommitteeMembers.forEach(this::assignCommitteeMember);
        this.finalComment = finalComment;
    }

    /**
     * Assign committee member to the application
     * @param user the committee member to assign
     * @throws ApplicationException if the user does not have REVIEW_APPLICATIONS permissions
     */
    public void assignCommitteeMember(User user) {
        assignCommitteeMember(user, assignedCommitteeMembers);
    }

    /**
     * Get an unmodifiable view of this application's committee members
     * @return the unmodifiable list of assigned committee members
     */
    public List<User> getAssignedCommitteeMembers() {
        return Collections.unmodifiableList(assignedCommitteeMembers);
    }

    /**
     * Assign the committee member and check their permissions to the user list.
     * @param member the member to add
     * @param userList the list of members to add to
     * @throws ApplicationException if the member does not have the correct permissions
     */
    private static void assignCommitteeMember(User member, List<User> userList) {
        if (!member.getRole().getPermissions().contains(Permissions.REVIEW_APPLICATIONS))
            throw new ApplicationException("The user being assigned to the SubmittedApplication must have the REVIEW_APPLICATIONS permission");

        userList.add(member);
    }

    /**
     * Assigns all this application's assigned committee members to the previous committee members
     * @throws InvalidStatusException if the application is not in a resubmitted status
     */
    public void assignCommitteeMembersToPrevious() {
        if (status != ApplicationStatus.RESUBMITTED)
            throw new InvalidStatusException("You cannot assign the assigned committee members to the previous members list if the " +
                    "status is not " + ApplicationStatus.RESUBMITTED);

        assignedCommitteeMembers.forEach(member -> assignCommitteeMember(member, previousCommitteeMembers));
        assignedCommitteeMembers.clear();
    }

    /**
     * Get an unmodifiable view of this application's previous committee members
     * @return the unmodifiable list of previous committee members before it was referred.
     */
    public List<User> getPreviousCommitteeMembers() {
        return Collections.unmodifiableList(previousCommitteeMembers);
    }

    /**
     * Clear the list of previous committee members
     */
    public void clearPreviousCommitteeMembers() {
        previousCommitteeMembers.clear();
    }

    /**
     * Adds the provided comment to the application
     * @param comment the comment to add
     */
    public void addComment(Comment comment) {
        this.comments.put(comment.getComponentId(), comment);
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
     * Set the status of the application. The status an application can be in differs depending on the concrete sub-class.
     *
     * @param status the status of the application
     * @throws InvalidStatusException if the status is invalid for that application
     */
    @Override
    public void setStatus(ApplicationStatus status) throws InvalidStatusException {
        if (status != null) {
            Set<ApplicationStatus> permissible = Set.of(ApplicationStatus.SUBMITTED, ApplicationStatus.RESUBMITTED,
                    ApplicationStatus.REVIEW, ApplicationStatus.REVIEWED,
                    ApplicationStatus.APPROVED, ApplicationStatus.REJECTED);
            if (!permissible.contains(status)) // TODO decide if approved/rejected require their own subclasses
                throw new InvalidStatusException("The only applicable statuses for a SubmittedApplication are " + permissible);

            this.status = status;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        SubmittedApplication that = (SubmittedApplication) o;
        return Objects.equals(id, that.id) && Objects.equals(applicationId, that.applicationId) && Objects.equals(user, that.user)
                && Objects.equals(applicationTemplate, that.applicationTemplate) && Objects.equals(answers, that.answers)
                && Objects.equals(comments, that.comments) && Objects.equals(assignedCommitteeMembers, that.assignedCommitteeMembers)
                && Objects.equals(finalComment, that.finalComment) && Objects.equals(previousCommitteeMembers, that.previousCommitteeMembers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, user, status, applicationTemplate, answers, comments, assignedCommitteeMembers, finalComment, previousCommitteeMembers);
    }
}
