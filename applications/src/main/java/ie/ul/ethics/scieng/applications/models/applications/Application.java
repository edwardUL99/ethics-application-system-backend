package ie.ul.ethics.scieng.applications.models.applications;

import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.users.models.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
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
     * The database ID of the application
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    /**
     * The unique ethics committee application ID
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
        this.id = id;
        this.applicationId = applicationId;
        this.user = user;
        this.status = status;
        this.applicationTemplate = applicationTemplate;
        this.answers = answers;
    }

    /**
     * This method determines if the provided user can view this application
     * @param user the user that wishes to view the application
     * @return true if they can view it, false if not
     */
    public abstract boolean canBeViewedBy(User user);
}
