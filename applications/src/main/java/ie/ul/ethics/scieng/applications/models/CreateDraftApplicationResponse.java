package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private Long id;
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
     * Create the response from the provided draft application
     * @param draftApplication the application to make the response from
     */
    public CreateDraftApplicationResponse(DraftApplication draftApplication) {
        this.id = draftApplication.getId();
        this.username = draftApplication.getUser().getUsername();
        this.templateId = draftApplication.getApplicationTemplate().getDatabaseId();
        this.createdAt = draftApplication.getLastUpdated();
    }
}
