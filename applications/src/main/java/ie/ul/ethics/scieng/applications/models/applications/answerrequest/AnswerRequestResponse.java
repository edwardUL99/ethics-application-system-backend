package ie.ul.ethics.scieng.applications.models.applications.answerrequest;

import ie.ul.ethics.scieng.applications.models.ApplicationResponse;
import ie.ul.ethics.scieng.applications.models.ApplicationResponseFactory;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.users.models.UserResponse;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents the response to a retrieval of an answer request
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AnswerRequestResponse {
    /**
     * The ID of the request
     */
    private Long id;
    /**
     * The application the request is associated with
     */
    private ApplicationResponse application;
    /**
     * The user that the answers are requested from
     */
    private UserResponse user;
    /**
     * The components to answer
     */
    private List<ApplicationComponent> components;
    /**
     * The timestamp of then the request was requested at
     */
    private LocalDateTime requestedAt;

    /**
     * Create a response from the provided answer request
     * @param answerRequest the request to create an answer response from
     */
    public AnswerRequestResponse(AnswerRequest answerRequest) {
        this.id = answerRequest.getId();
        this.application = ApplicationResponseFactory.buildResponse(answerRequest.getApplication());
        this.user = new UserResponse(answerRequest.getUser());
        this.components = answerRequest.getComponents();
        this.requestedAt = answerRequest.getRequestedAt();
    }
}

