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

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.util.*;

/**
 * This application represents an application that has been referred back to the applicant
 */
@Entity
@Getter
@Setter
public class ReferredApplication extends SubmittedApplication {
    /**
     * The list of field component IDs that can be edited in the referred application
     */
    @ElementCollection
    private List<String> editableFields;
    /**
     * The user that referred the application
     */
    @OneToOne
    @Setter(AccessLevel.NONE)
    private User referredBy;

    /**
     * Create a default Application
     */
    public ReferredApplication() {
        this(null, null, null, null, new HashMap<>(),
                new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>(), null);
    }

    /**
     * Create an Application
     *
     * @param id                       the database ID of the application
     * @param applicationId            the ethics committee application ID
     * @param user                     the user that owns the application
     * @param applicationTemplate      the template that this application was answered on
     * @param answers                   the answers to the application
     * @param comments                 the list of comments on this application
     * @param assignedCommitteeMembers the list of assigned committee members
     * @param finalComment             the last comment left on the application
     * @param editableFields           the list of component IDs that can be edited in the referred application
     * @param referredBy               the user that referred the application (must have ADMIN permission)
     */
    public ReferredApplication(Long id, String applicationId, User user,
                               ApplicationTemplate applicationTemplate, Map<String, Answer> answers, List<ApplicationComments> comments,
                               List<AssignedCommitteeMember> assignedCommitteeMembers, Comment finalComment, List<String> editableFields, User referredBy) {
        this(id, applicationId, user, applicationTemplate, answers, new ArrayList<>(), comments, assignedCommitteeMembers,
                finalComment, editableFields, referredBy);
    }

    /**
     * Create an Application
     *
     * @param id                       the database ID of the application
     * @param applicationId            the ethics committee application ID
     * @param user                     the user that owns the application
     * @param applicationTemplate      the template that this application was answered on
     * @param answers                   the answers to the application
     * @param attachedFiles             the ist of attached files
     * @param comments                 the list of comments on this application
     * @param assignedCommitteeMembers the list of assigned committee members
     * @param finalComment             the last comment left on the application
     * @param editableFields           the list of component IDs that can be edited in the referred application
     * @param referredBy               the user that referred the application (must have ADMIN permission)
     */
    public ReferredApplication(Long id, String applicationId, User user,
                               ApplicationTemplate applicationTemplate, Map<String, Answer> answers, List<AttachedFile> attachedFiles, List<ApplicationComments> comments,
                               List<AssignedCommitteeMember> assignedCommitteeMembers, Comment finalComment, List<String> editableFields, User referredBy) {
        super(id, applicationId, user, ApplicationStatus.REFERRED, applicationTemplate, answers, attachedFiles, comments, assignedCommitteeMembers, finalComment);
        this.editableFields = editableFields;
        this.setReferredBy(referredBy);
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
            if (status != ApplicationStatus.REFERRED)
                throw new InvalidStatusException("The only applicable state to a ReferredApplication is " + ApplicationStatus.REFERRED);
        }

        this.status = status;
    }

    /**
     * Set the referred by user. Must have ADMIN permission
     * @param referredBy the user that referred the application
     */
    public void setReferredBy(User referredBy) {
        if (referredBy != null && !referredBy.getRole().getPermissions().contains(Permissions.REFER_APPLICATIONS))
            throw new ApplicationException("The referredBy user must contain the REFER_APPLICATION permission");

        this.referredBy = referredBy;
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
    public ReferredApplication clean(User user) {
        ReferredApplication application = copy();
        Collection<Permission> permissions = user.getRole().getPermissions();
        boolean review = permissions.contains(Permissions.REVIEW_APPLICATIONS);

        if (!review) {
            application.assignedCommitteeMembers.clear();
            application.previousCommitteeMembers.clear();
        }

        application.comments = filterComments(application.comments, permissions, user);

        return application;
    }

    /**
     * Make a copy of this application instance from top-level fields. If any fields are nested objects,
     * they should be shallow copied, for example, a list will be a copy of the list but same objects contained within it.
     *
     * @return the copied instance
     */
    @Override
    public ReferredApplication copy() {
        ReferredApplication referred = new ReferredApplication(id, applicationId, user, applicationTemplate, new HashMap<>(answers),
                new ArrayList<>(attachedFiles), new ArrayList<>(),
                new ArrayList<>(assignedCommitteeMembers), finalComment, new ArrayList<>(editableFields), referredBy);
        referred.comments = comments;
        referred.accessList = new ArrayList<>(referred.accessList);
        referred.setLastUpdated(lastUpdated);
        referred.setApprovalTime(approvalTime);
        referred.setSubmittedTime(submittedTime);

        return referred;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReferredApplication that = (ReferredApplication) o;
        return Objects.equals(id, that.id) && Objects.equals(applicationId, that.applicationId) && Objects.equals(user, that.user)
                && Objects.equals(applicationTemplate, that.applicationTemplate) && Objects.equals(answers, that.answers)
                && Objects.equals(attachedFiles, that.attachedFiles)
                && Objects.equals(editableFields, that.editableFields) && Objects.equals(referredBy, that.referredBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, user, applicationTemplate, answers, attachedFiles, editableFields, referredBy);
    }
}
