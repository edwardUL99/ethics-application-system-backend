package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstract class provides the "interface" (has to be an abstract class for hibernate) for applications in the system.
 * It provides a base for an application at different stages/statuses of application management.
 * Contains operations that provide a no-op implementation that can be implemented by sub-classes where it makes sense
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public abstract class Application {
    /**
     * The database ID of the application. It changes depending on the status of the application since a different
     * subclass is instantiated. The old application should be deleted and replaced with the new one. However, the
     * {@link #applicationId} should remain the same
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    /**
     * The unique ethics committee application ID. This should remain the same across different Application sub-class
     * instances, even if {@link #id} remains the same, as long as it represents the same application, just in a different state
     */
    @Column(unique = true)
    protected String applicationId;
    /**
     * The user that owns this application
     */
    @OneToOne
    protected User user;
    /**
     * The status of the application
     */
    @Setter(AccessLevel.NONE)
    protected ApplicationStatus status;
    /**
     * The template of the application being filled in
     */
    @OneToOne
    protected ApplicationTemplate applicationTemplate;
    /**
     * The map of component IDs to the values (i.e. the answers)
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "values_mapping",
            joinColumns = {@JoinColumn(name = "database_ID", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "values_id", referencedColumnName = "id")})
    @MapKey(name = "componentId")
    protected Map<String, Answer> answers;
    /**
     * The list of files attached to the application
     */
    @OneToMany(cascade = CascadeType.ALL)
    protected List<AttachedFile> attachedFiles;
    /**
     * The timestamp of when the application was last updated
     */
    protected LocalDateTime lastUpdated;
    /**
     * A list of users with access to the application if they need to provide input to it. Access only for answer requests.
     * Any other contexts (i.e. retrieving viewable applications), should be blocked
     */
    @OneToMany(cascade = CascadeType.ALL)
    protected List<UserAccess> accessList;

    /**
     * Create a default Application
     */
    protected Application() {
        this(null, null, null, null, null, new HashMap<>());
    }

    /**
     * Create an Application
     * @param id the database ID of the application
     * @param applicationId the ethics committee application ID
     * @param user the user that owns the application
     * @param status the status of the application
     * @param applicationTemplate the template that this application was answered on
     * @param answers the answers to the application
     */
    protected Application(Long id, String applicationId, User user, ApplicationStatus status, ApplicationTemplate applicationTemplate, Map<String, Answer> answers) {
        this(id, applicationId, user, status, applicationTemplate, answers, new ArrayList<>());
    }

    /**
     * Create an Application
     * @param id the database ID of the application
     * @param applicationId the ethics committee application ID
     * @param user the user that owns the application
     * @param status the status of the application
     * @param applicationTemplate the template that this application was answered on
     * @param answers the answers to the application
     * @param attachedFiles a list of attached files
     */
    protected Application(Long id, String applicationId, User user, ApplicationStatus status, ApplicationTemplate applicationTemplate, Map<String, Answer> answers,
                          List<AttachedFile> attachedFiles) {
        this.id = id;
        this.applicationId = applicationId;
        this.user = user;
        this.setStatus(status);
        this.applicationTemplate = applicationTemplate;
        this.answers = answers;
        this.attachedFiles = new ArrayList<>();
        attachedFiles.forEach(this::attachFile);
        this.accessList = new ArrayList<>();
    }

    /**
     * Attach the given file to the application
     * @param file the file to attach
     */
    public void attachFile(AttachedFile file) {
        for (AttachedFile attachedFile : this.attachedFiles)
            if (attachedFile.getDirectory().equals(file.getDirectory()) && attachedFile.getUsername().equals(file.getUsername())
                && attachedFile.getFilename().equals(file.getFilename()))
                return;

        this.attachedFiles.add(file);
    }

    /**
     * Grants the given user access to the application
     * @param user the user to allow access
     */
    public void grantUserAccess(User user) {
        UserAccess access = new UserAccess(null, user);

        if (!this.accessList.contains(access)) {
            this.accessList.add(access);
        }
    }

    /**
     * Remove the user from the access list
     * @param user the user to remove
     */
    public void removeUserAccess(User user) {
        this.accessList.remove(new UserAccess(null, user));
    }

    /**
     * Assign committee member to the application
     * @param user the committee member to assign
     * @throws ApplicationException if the user does not have REVIEW_APPLICATIONS permissions
     */
    public void assignCommitteeMember(User user) {
        // no-op in base application
    }

    /**
     * Set the list of assigned committee members
     * @param assignedCommitteeMembers the new list of assigned committee members
     */
    public void setAssignedCommitteeMembers(List<AssignedCommitteeMember> assignedCommitteeMembers) {
        // no-op in base application
    }

    /**
     * Get an unmodifiable view of this application's committee members
     * @return the unmodifiable list of assigned committee members
     */
    public List<AssignedCommitteeMember> getAssignedCommitteeMembers() {
        // no-op in base application
        return Collections.emptyList();
    }

    /**
     * Assigns all this application's assigned committee members to the previous committee members
     * @throws InvalidStatusException if the application is not in a resubmitted status
     */
    public void assignCommitteeMembersToPrevious() {
        // no-op in base application
    }

    /**
     * Get an unmodifiable view of this application's previous committee members
     * @return the unmodifiable list of previous committee members before it was referred.
     */
    public List<User> getPreviousCommitteeMembers() {
        // no-op in base application
        return Collections.emptyList();
    }

    /**
     * Clear the list of previous committee members
     */
    public void clearPreviousCommitteeMembers() {
        // no-op in base application
    }

    /**
     * Adds the provided comment to the application
     * @param comment the comment to add
     */
    public void addComment(Comment comment) {
        // no-op in base application
    }

    /**
     * Gets the comments added to the application
     * @return the map of comments added to the application
     */
    public Map<String, ApplicationComments> getComments() {
        // no-op in base application
        return Collections.emptyMap();
    }

    /**
     * Sets the comments of the application
     * @param comments the comments to set
     */
    public void setComments(Map<String, ApplicationComments> comments) {
        // no-op in base application
    }

    /**
     * Get the final comment left on the application after approval/rejection
     * @return the final comment left
     */
    public Comment getFinalComment() {
        // no-op in base application
        return null;
    }

    /**
     * Set the final comment of the application
     * @param finalComment the last comment of the application if it makes sense for the application status
     */
    public void setFinalComment(Comment finalComment) {
        // no-op in base application
    }

    /**
     * Get the timestamp of when the application was submitted
     * @return the timestamp of when the application was submitted
     */
    public LocalDateTime getSubmittedTime() {
        // no-op in base application
        return null;
    }

    /**
     * Set the timestamp of when the application was submitted
     * @param submittedTime the time when the application was submitted
     */
    public void setSubmittedTime(LocalDateTime submittedTime) {
        // no-op in base application
    }

    /**
     * Get the timestamp of when the application was approved/rejected
     * @return timestamp of when an approval decision was made
     */
    public LocalDateTime getApprovalTime() {
        // no-op in base application
        return null;
    }

    /**
     * Sets the approval timestamp of the application
     * @param approvalTime approval/rejection timestamp
     */
    public void setApprovalTime(LocalDateTime approvalTime) {
        // no-op in base application
    }

    /**
     * Gets the list of field component IDs that can be edited in this state
     * @return the list of field IDs that can be edited in this application status
     */
    public List<String> getEditableFields() {
        // no-op in base application
        return Collections.emptyList();
    }

    /**
     * Set the list of editable field component IDs
     * @param editableFields the list of editable field component IDs. If these IDs don't exist
     *                       within the application template, they won't be rendered in the front-end as editable
     */
    public void setEditableFields(List<String> editableFields) {
        // no-op in base application
    }

    /**
     * If the application has been referred, this method gets the user that referred the application
     * @return the user that referred the application
     */
    public User getReferredBy() {
        // no-op in base application
        return null;
    }

    /**
     * Set the user that referred the application
     * @param referredBy the user that referred the application
     */
    public void setReferredBy(User referredBy) {
        // no-op in base application
    }

    /**
     * This method determines if the provided user can view this application without checking answer lists
     * @param user the user that wishes to view the application
     * @return true if they can view it, false if not
     */
    public abstract boolean canBeViewedBy(User user);

    /**
     * This method determines if the application can be viewed by the user
     * @param user the user that wished to view the application
     * @param answerRequest true to determine if the user can access the application in an answer request context (by checking access list), otherwise just check
     *                      permissions and credentials
     * @return true if they can view it, false if not
     */
    public abstract boolean canBeViewedBy(User user, boolean answerRequest);

    /**
     * Set the status of the application. The status an application can be in differs depending on the concrete sub-class.
     * @param status the status of the application
     * @throws ApplicationException if the status is invalid for that application
     */
    public abstract void setStatus(ApplicationStatus status) throws ApplicationException;

    /**
     * If this application is being used in a response, it should be "cleaned" to remove information from it
     * that may not be viewable by the user depending on their permissions. If the user can view everything regardless of
     * permissions, this method can safely be a no-op
     * If the method does need to clean an application, {@link #copy()} should be called, modify the copy and return it
     * @param user the user that will be viewing the application
     * @return the cleaned application. If no-op this could be the same instance as this
     */
    public abstract Application clean(User user);

    /**
     * Make a copy of this application instance from top-level fields. If any fields are nested objects,
     * they should be shallow copied, for example, a list will be a copy of the list but same objects contained within it.
     * @return the copied instance
     */
    public abstract Application copy();
}
