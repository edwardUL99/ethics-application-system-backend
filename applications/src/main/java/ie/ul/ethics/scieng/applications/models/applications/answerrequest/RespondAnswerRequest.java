package ie.ul.ethics.scieng.applications.models.applications.answerrequest;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * This class represents a request sent by the user that answered all the questions referred to them
 */
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class RespondAnswerRequest {
    /**
     * The database ID of the answer request
     */
    @NotNull
    private Long requestId;
    /**
     * The answers sent by the requested user
     */
    @NotNull
    private Map<String, Answer> answers;
}
