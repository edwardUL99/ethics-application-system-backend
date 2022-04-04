package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.AttachedFile;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a request to update the draft/referred application
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UpdateDraftApplicationRequest {
    /**
     * The application id of the application being updated (REC number)
     */
    @NotNull
    private String id;
    /**
     * The new answers being saved
     */
    @NotNull
    private Map<String, Answer> answers;
    /**
     * The new file attachments being added
     */
    private List<AttachedFile> attachedFiles = new ArrayList<>();
    /**
     * The template being updated
     */
    @NotNull
    private ApplicationTemplate template;

    /**
     * Create a default request object with the non-null fields initialised
     * @param id the id of the application to update
     * @param answers the answers for the application
     * @param template the template being updated
     */
    public UpdateDraftApplicationRequest(String id, Map<String, Answer> answers, ApplicationTemplate template) {
        this.id = id;
        this.answers = answers;
        this.attachedFiles = new ArrayList<>();
        this.template = template;
    }
}
