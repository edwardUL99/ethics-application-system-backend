package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.AttachedFile;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * This class represents a response for an application
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public abstract class ApplicationResponse {
    /**
     * The database ID for the application
     */
    protected Long dbId;
    /**
     * The ID of the application
     */
    protected String id;
    /**
     * The username of the user that created the application
     */
    protected String username;
    /**
     * The status of the application
     */
    protected ApplicationStatus status;
    /**
     * The ID of the application template
     */
    protected Long templateId;
    /**
     * The answers given on the application
     */
    protected Map<String, Answer> answers;
    /**
     * The timestamp of when the application was last updated
     */
    protected LocalDateTime lastUpdated;
    /**
     * The files attached to the application
     */
    protected List<AttachedFile> attachedFiles;

    /**
     * Create a response from the application
     * @param application the application to create the response from
     */
    public ApplicationResponse(Application application) {
        validateApplicationStatus(application);

        this.dbId = application.getId();
        this.id = application.getApplicationId();
        this.username = application.getUser().getUsername();
        this.status = application.getStatus();
        this.templateId = application.getApplicationTemplate().getDatabaseId();
        this.answers = application.getAnswers();
        this.lastUpdated = application.getLastUpdated();
        this.attachedFiles = application.getAttachedFiles();
    }

    /**
     * Validate that the application has the correct status for this response object
     * @param application the application the response is being created from
     * @throws InvalidStatusException if not valid
     */
    protected abstract void validateApplicationStatus(Application application) throws InvalidStatusException;
}
