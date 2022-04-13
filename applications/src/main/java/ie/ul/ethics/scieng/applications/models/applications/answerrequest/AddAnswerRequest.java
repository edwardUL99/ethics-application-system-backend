package ie.ul.ethics.scieng.applications.models.applications.answerrequest;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * This class represents a request to add a new Supervisor request to the system
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AddAnswerRequest {
    /**
     * The ID of the application
     */
    @NotNull
    private String id;
    /**
     * The username of the supervisor
     */
    @NotNull
    private String username;
    /**
     * The list of components to request
     */
    @NotNull
    private List<ApplicationComponent> components;
}
