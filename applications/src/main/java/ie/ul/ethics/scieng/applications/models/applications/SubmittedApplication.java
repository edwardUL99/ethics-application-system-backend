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
import java.time.LocalDateTime;
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
    @OneToMany(cascade = CascadeType.ALL)
    @Getter(AccessLevel.NONE)
    protected List<AssignedCommitteeMember> assignedCommitteeMembers;
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
     * The timestamp of when the application was approved
     */
    protected LocalDateTime approvalTime;

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
                                List<Comment> comments, List<AssignedCommitteeMember> assignedCommitteeMembers, Comment finalComment) {
        this(id, applicationId, user, status, applicationTemplate, answers, new ArrayList<>(), comments, assignedCommitteeMembers,
                finalComment);
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
     * @param attachedFiles         the list of attached files
     * @param comments            the list of comments on this application
     * @param assignedCommitteeMembers the list of assigned committee members
     * @param finalComment        the final comment given to the application if approved/rejected
     */
    public SubmittedApplication(Long id, String applicationId, User user, ApplicationStatus status,
                                ApplicationTemplate applicationTemplate, Map<String, Answer> answers, List<AttachedFile> attachedFiles,
                                List<Comment> comments, List<AssignedCommitteeMember> assignedCommitteeMembers, Comment finalComment) {
        super(id, applicationId, user, status, applicationTemplate, answers, attachedFiles);
        this.comments = new HashMap<>();
        comments.forEach(this::addComment);
        this.assignedCommitteeMembers = assignedCommitteeMembers;
        this.finalComment = finalComment;
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
     * @param attachedFiles         the list of attached files
     * @param comments            the list of comments on this application
     * @param assignedCommitteeMembers the list of assigned committee members
     * @param finalComment        the final comment given to the application if approved/rejected
     * @param approvalTime        the timestamp of when the application was approved
     */
    public SubmittedApplication(Long id, String applicationId, User user, ApplicationStatus status,
                                ApplicationTemplate applicationTemplate, Map<String, Answer> answers, List<AttachedFile> attachedFiles,
                                List<Comment> comments, List<AssignedCommitteeMember> assignedCommitteeMembers, Comment finalComment, LocalDateTime approvalTime) {
        super(id, applicationId, user, status, applicationTemplate, answers, attachedFiles);
        this.comments = new HashMap<>();
        comments.forEach(this::addComment);
        this.assignedCommitteeMembers = assignedCommitteeMembers;
        this.finalComment = finalComment;
        this.approvalTime = approvalTime;
    }

    /**
     * Assign committee member to the application
     * @param user the committee member to assign
     * @throws ApplicationException if the user does not have REVIEW_APPLICATIONS permissions
     */
    public void assignCommitteeMember(User user) {
        verifyMemberReview(user);
        this.assignedCommitteeMembers.add(new AssignedCommitteeMember(null, user, false));
    }

    /**
     * Get an unmodifiable view of this application's committee members
     * @return the unmodifiable list of assigned committee members
     */
    public List<AssignedCommitteeMember> getAssignedCommitteeMembers() {
        return Collections.unmodifiableList(assignedCommitteeMembers);
    }

    /**
     * Verify that the member can review an application
     * @param member the member to verify
     */
    private static void verifyMemberReview(User member) {
        if (!member.getRole().getPermissions().contains(Permissions.REVIEW_APPLICATIONS))
            throw new ApplicationException("The user being assigned to the SubmittedApplication must have the REVIEW_APPLICATIONS permission");
    }

    /**
     * Assign the committee member and check their permissions to the user list.
     * @param member the member to add
     * @param userList the list of members to add to
     * @throws ApplicationException if the member does not have the correct permissions
     */
    private static void assignCommitteeMember(User member, List<User> userList) {
        verifyMemberReview(member);
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

        assignedCommitteeMembers.forEach(member -> assignCommitteeMember(member.getUser(), previousCommitteeMembers));
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

        boolean isAssigned = assignedCommitteeMembers.stream().map(AssignedCommitteeMember::getUser)
                .anyMatch(u -> u.equals(user));

        return (this.user.getUsername().equals(user.getUsername()) && permissions.contains(Permissions.VIEW_OWN_APPLICATIONS))
                || (permissions.contains(Permissions.REVIEW_APPLICATIONS) && isAssigned)
                || permissions.contains(Permissions.VIEW_ALL_APPLICATIONS);
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
     * If this application is being used in a response, it should be "cleaned" to remove information from it
     * that may not be viewable by the user depending on their permissions. If the user can view everything regardless of
     * permissions, this method can safely be a no-op
     * If the method does need to clean an application, {@link #copy()} should be called, modify the copy and return it
     *
     * @param user the user that will be viewing the application
     * @return the cleaned application. If no-op this could be the same instance as this
     */
    @Override
    public SubmittedApplication clean(User user) {
        SubmittedApplication application = copy();
        Collection<Permission> permissions = user.getRole().getPermissions();

        if (status == ApplicationStatus.SUBMITTED || status == ApplicationStatus.REVIEW || status == ApplicationStatus.REVIEWED) {
            if (!permissions.contains(Permissions.REVIEW_APPLICATIONS)) {
                application.comments.clear();
                application.finalComment = null;
            }
        } else if (status == ApplicationStatus.RESUBMITTED) {
            if (!permissions.contains(Permissions.REVIEW_APPLICATIONS)) {
                application.comments.clear();
                application.finalComment = null;
                application.previousCommitteeMembers.clear();
            }
        }

        return application;
    }

    /**
     * Make a copy of this application instance from top-level fields. If any fields are nested objects,
     * they should be shallow copied, for example, a list will be a copy of the list but same objects contained within it.
     *
     * @return the copied instance
     */
    @Override
    public SubmittedApplication copy() {
        SubmittedApplication submitted = new SubmittedApplication(id, applicationId, user, status, applicationTemplate, new HashMap<>(answers),
                new ArrayList<>(attachedFiles.values()), new ArrayList<>(comments.values()), new ArrayList<>(assignedCommitteeMembers), finalComment, approvalTime);
        submitted.previousCommitteeMembers = new ArrayList<>(submitted.previousCommitteeMembers);
        submitted.setLastUpdated(lastUpdated);

        return submitted;
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
                && Objects.equals(attachedFiles, that.attachedFiles)
                && Objects.equals(comments, that.comments) && Objects.equals(assignedCommitteeMembers, that.assignedCommitteeMembers)
                && Objects.equals(finalComment, that.finalComment) && Objects.equals(previousCommitteeMembers, that.previousCommitteeMembers)
                && Objects.equals(approvalTime, that.approvalTime);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, user, status, applicationTemplate, answers, attachedFiles, comments,
                assignedCommitteeMembers, finalComment, previousCommitteeMembers, approvalTime);
    }

}
