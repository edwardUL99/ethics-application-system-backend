package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * This class represents a request to patch answers on an application (can be in any state)
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PatchAnswersRequest {
    /**
     * The ID of the application
     */
    private String id;
    /**
     * The map of answers to patch
     */
    private Map<String, Answer> answers;
}
