package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This class represents a response to create a draft application
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CreateDraftApplicationResponse {
    /**
     * The database ID of the created draft application
     */
    private Long dbId;
    /**
     * The application ID for the application
     */
    private String id;
    /**
     * The status of a draft application is always DRAFT
     */
    private final ApplicationStatus status = ApplicationStatus.DRAFT;
    /**
     * The username of the user that created the draft application
     */
    private String username;
    /**
     * The ID of the saved application template
     */
    private Long templateId;
    /**
     * The timestamp of when the application was created
     */
    private LocalDateTime createdAt;
    /**
     * The answers saved to the database
     */
    private Map<String, Answer> answers;

    /**
     * Create the response from the provided draft application
     * @param draftApplication the application to make the response from
     */
    public CreateDraftApplicationResponse(Application draftApplication) {
        this.dbId = draftApplication.getId();
        this.id = draftApplication.getApplicationId();
        this.username = draftApplication.getUser().getUsername();
        this.templateId = draftApplication.getApplicationTemplate().getDatabaseId();
        this.createdAt = draftApplication.getLastUpdated();
        this.answers = draftApplication.getAnswers();
    }
}
