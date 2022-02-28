package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class represents a request to create a new draft application with an initial set of values
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class CreateDraftApplicationRequest {
    /**
     * The username of the user creating the application
     */
    @NotNull
    private String username;
    /**
     * The template of the application being used
     */
    @NotNull
    private ApplicationTemplate applicationTemplate;
    /**
     * The initial mapping of values
     */
    @NotNull
    private Map<String, Answer> answers;
}
