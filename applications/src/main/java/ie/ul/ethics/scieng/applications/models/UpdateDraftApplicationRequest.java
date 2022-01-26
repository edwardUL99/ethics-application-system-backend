package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class represents a request to update the draft application request
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
     * The new values being saved
     */
    @NotNull
    private Map<String, Answer> values;
}
