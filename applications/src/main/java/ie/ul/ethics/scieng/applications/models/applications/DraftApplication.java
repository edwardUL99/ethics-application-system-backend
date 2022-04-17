package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.authorization.Permissions;
import ie.ul.ethics.scieng.users.models.User;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

/**
 * This represents an application that is a draft
 */
@Entity
@Getter
@Setter
public class DraftApplication extends Application {
    /**
     * Create a default DraftApplication
     */
    public DraftApplication() {
        this(null, null, null, null, new HashMap<>());
    }

    /**
     * Create a DraftApplication
     * @param id the database ID of the object
     * @param applicationId the ethics committee application ID
     * @param user the user that owns this application
     * @param applicationTemplate the template the application is being created to
     * @param answers the map of component IDs to the answers object
     */
    public DraftApplication(Long id, String applicationId, User user, ApplicationTemplate applicationTemplate, Map<String, Answer> answers) {
        this(id, applicationId, user, applicationTemplate, answers, new ArrayList<>());
    }

    /**
     * Create a DraftApplication
     *
     * @param id                  the database ID of the application
     * @param applicationId       the ethics committee application ID
     * @param user                the user that owns the application
     * @param applicationTemplate the template that this application was answered on
     * @param answers             the answers to the application
     * @param attachedFiles       a list of attached files
     */
    public DraftApplication(Long id, String applicationId, User user, ApplicationTemplate applicationTemplate, Map<String, Answer> answers, List<AttachedFile> attachedFiles) {
        super(id, applicationId, user, ApplicationStatus.DRAFT, applicationTemplate, answers, attachedFiles);
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
            if (status != ApplicationStatus.DRAFT)
                throw new InvalidStatusException("The only applicable state to a DraftApplication is " + ApplicationStatus.DRAFT);
        }

        this.status = status;
    }

    /**
     * This method determines if the provided user can view this application
     *
     * @param user the user that wishes to view the application
     * @return true if they can view it, false if not
     */
    @Override
    public boolean canBeViewedBy(User user) {
        return canBeViewedBy(user, false);
    }

    /**
     * This method determines if the application can be viewed by the user
     *
     * @param user          the user that wished to view the application
     * @param answerRequest true to determine if the user can access the application in an answer request context (by checking access list), otherwise just check
     *                      permissions and credentials
     * @return true if they can view it, false if not
     */
    @Override
    public boolean canBeViewedBy(User user, boolean answerRequest) {
        return (answerRequest && accessList.contains(new UserAccess(null, user))) || this.user.getUsername().equals(user.getUsername()) &&
                user.getRole().getPermissions().contains(Permissions.VIEW_OWN_APPLICATIONS);
    }

    /**
     * If this application is being used in a response, it should be "cleaned" to remove information from it
     * that may not be viewable by the user depending on their permissions. If the user can view everything regardless of
     * permissions, this method can safely be a no-op
     * If the method does need to clean an application, {@link #copy()} should be called, modify the copy and return it
     * @param user the user that will be viewing the application
     * @return the cleaned application. If no-op this could be the same instance as this
     */
    @Override
    public DraftApplication clean(User user) {
        // no-op for draft application
        return this;
    }

    /**
     * Make a copy of this application instance from top-level fields. If any fields are nested objects,
     * they should be shallow copied, for example, a list will be a copy of the list but same objects contained within it.
     *
     * @return the copied instance
     */
    @Override
    public DraftApplication copy() {
        DraftApplication draft = new DraftApplication(id, applicationId, user, applicationTemplate, new HashMap<>(answers), new ArrayList<>(attachedFiles));
        draft.setLastUpdated(lastUpdated);
        draft.accessList = new ArrayList<>(accessList);

        return draft;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DraftApplication that = (DraftApplication) o;
        return Objects.equals(id, that.id) && Objects.equals(applicationId, that.applicationId) && Objects.equals(user, that.user)
                && Objects.equals(applicationTemplate, that.applicationTemplate) && Objects.equals(answers, that.answers)
                && Objects.equals(attachedFiles, that.attachedFiles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, user, applicationTemplate, answers, attachedFiles);
    }
}
