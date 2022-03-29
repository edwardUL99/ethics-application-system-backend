package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
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
 * This class represents a response to a request to update a draft application
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDraftApplicationResponse {
    /**
     * A message to send back in the response
     */
    private String message;
    /**
     * The answers from the updated draft application
     */
    private Map<String, Answer> answers;
    /**
     * The timestamp indicating when the application was last updated
     */
    private LocalDateTime lastUpdated;
    /**
     * The list of files attached to the application
     */
    private List<AttachedFile> attachedFiles;
}
