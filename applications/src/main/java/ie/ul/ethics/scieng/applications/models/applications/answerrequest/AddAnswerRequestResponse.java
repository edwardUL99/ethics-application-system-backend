package ie.ul.ethics.scieng.applications.models.applications.answerrequest;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * This class represents a response to the add supervisor request
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AddAnswerRequestResponse {
    /**
     * The ID of the application
     */
    private String id;
    /**
     * The username of the user
     */
    private String user;
    /**
     * The timestamp of when the request was made
     */
    private LocalDateTime requestedAt;

    /**
     * Create a response from the given request
     * @param request the request to create the response from
     */
    public AddAnswerRequestResponse(AnswerRequest request) {
        this.id = request.getApplication().getApplicationId();
        this.user = request.getUser().getUsername();
        this.requestedAt = request.getRequestedAt();
    }
}
