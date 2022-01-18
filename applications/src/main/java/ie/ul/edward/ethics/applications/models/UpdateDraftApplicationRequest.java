package ie.ul.edward.ethics.applications.models;

import ie.ul.edward.ethics.applications.models.applications.DraftApplication;
import lombok.*;

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
     * The id of the application being updated
     */
    @NotNull
    private Long id;
    /**
     * The new values being saved
     */
    @NotNull
    private Map<String, DraftApplication.Value> values;
}
