package ie.ul.edward.ethics.applications.models.applications;

import ie.ul.edward.ethics.users.models.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

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
     * The timestamp of when the application was last updated
     */
    protected LocalDateTime lastUpdated;

    /**
     * Create a default Application
     */
    public Application() {
        this(null, null, null, null);
    }

    /**
     * Create an Application
     * @param id the database ID of the application
     * @param applicationId the ethics committee application ID
     * @param user the user that owns the application
     * @param status the status of the application
     */
    public Application(Long id, String applicationId, User user, ApplicationStatus status) {
        this.id = id;
        this.applicationId = applicationId;
        this.user = user;
        this.status = status;
    }
}
