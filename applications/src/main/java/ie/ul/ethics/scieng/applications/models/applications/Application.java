package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.models.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This abstract class provides the "interface" (has to be an abstract class for hibernate) for applications in the system
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
     * The map of component IDs to the attached files
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "attachments_mapping",
            joinColumns = {@JoinColumn(name = "database_ID", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "files_id", referencedColumnName = "id")})
    @MapKey(name = "componentId")
    protected Map<String, AttachedFile> attachedFiles;
    /**
     * The timestamp of when the application was last updated
     */
    protected LocalDateTime lastUpdated;

    /**
     * Create a default Application
     */
    public Application() {
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
    public Application(Long id, String applicationId, User user, ApplicationStatus status, ApplicationTemplate applicationTemplate, Map<String, Answer> answers) {
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
    public Application(Long id, String applicationId, User user, ApplicationStatus status, ApplicationTemplate applicationTemplate, Map<String, Answer> answers,
                       List<AttachedFile> attachedFiles) {
        this.id = id;
        this.applicationId = applicationId;
        this.user = user;
        this.setStatus(status);
        this.applicationTemplate = applicationTemplate;
        this.answers = answers;
        this.attachedFiles = new HashMap<>();
        attachedFiles.forEach(this::attachFile);
    }

    /**
     * Attach the given file to the application
     * @param file the file to attach
     */
    public void attachFile(AttachedFile file) {
        this.attachedFiles.put(file.getComponentId(), file);
    }

    /**
     * This method determines if the provided user can view this application
     * @param user the user that wishes to view the application
     * @return true if they can view it, false if not
     */
    public abstract boolean canBeViewedBy(User user);

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
