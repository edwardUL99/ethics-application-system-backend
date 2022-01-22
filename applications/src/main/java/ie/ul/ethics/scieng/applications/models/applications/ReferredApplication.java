package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.User;
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
        this(null, null, null, ApplicationStatus.REFERRED, null, new HashMap<>(),
                new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>(), null);
    }

    /**
     * Create an Application
     *
     * @param id                       the database ID of the application
     * @param applicationId            the ethics committee application ID
     * @param user                     the user that owns the application
     * @param status                   the status of the application
     * @param applicationTemplate      the template that this application was answered on
     * @param answers                   the answers to the application
     * @param comments                 the list of comments on this application
     * @param assignedCommitteeMembers the list of assigned committee members
     * @param finalComment             the last comment left on the application
     * @param editableFields           the list of component IDs that can be edited in the referred application
     * @param referredBy               the user that referred the application (must have ADMIN permission)
     */
    public ReferredApplication(Long id, String applicationId, User user, ApplicationStatus status,
                               ApplicationTemplate applicationTemplate, Map<String, Answer> answers, List<Comment> comments,
                               List<User> assignedCommitteeMembers, Comment finalComment, List<String> editableFields, User referredBy) {
        super(id, applicationId, user, status, applicationTemplate, answers, comments, assignedCommitteeMembers, finalComment);
        this.editableFields = editableFields;
        this.setReferredBy(referredBy);
    }

    /**
     * Set the status of the application. The status an application can be in differs depending on the concrete sub-class.
     *
     * @param status the status of the application
     * @throws ApplicationException if the status is invalid for that application
     */
    @Override
    public void setStatus(ApplicationStatus status) throws ApplicationException {
        if (status != null) {
            if (status != ApplicationStatus.REFERRED)
                throw new ApplicationException("The only applicable state to a ReferredApplication is " + ApplicationStatus.REFERRED);
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReferredApplication that = (ReferredApplication) o;
        return Objects.equals(id, that.id) && Objects.equals(applicationId, that.applicationId) && Objects.equals(user, that.user)
                && Objects.equals(applicationTemplate, that.applicationTemplate) && Objects.equals(answers, that.answers)
                && Objects.equals(editableFields, that.editableFields) && Objects.equals(referredBy, that.referredBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, user, applicationTemplate, answers, editableFields, referredBy);
    }
}
