package ie.ul.ethics.scieng.applications.models.applications;

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
        super(id, applicationId, user, ApplicationStatus.DRAFT, applicationTemplate, answers);
    }

    /**
     * Overridden as you cannot change the status of a DraftApplication
     * This operation is a no-op
     */
    @Override
    public void setStatus(ApplicationStatus status) {
        this.status = ApplicationStatus.DRAFT;
    }

    /**
     * This method determines if the provided user can view this application
     *
     * @param user the user that wishes to view the application
     * @return true if they can view it, false if not
     */
    @Override
    public boolean canBeViewedBy(User user) {
        return this.user.getUsername().equals(user.getUsername()) &&
                user.getRole().getPermissions().contains(Permissions.VIEW_OWN_APPLICATIONS);
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
                && Objects.equals(applicationTemplate, that.applicationTemplate) && Objects.equals(answers, that.answers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, applicationId, user, applicationTemplate, answers);
    }
}
