package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AddAnswerRequest;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.RespondAnswerRequest;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AnswerRequest;
import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;

import java.util.List;

/**
 * This interface provides the service for allowing applicants to request other users to answer a question (e.g. a signature
 * etc.).
 */
public interface AnswerRequestService {
    /**
     * Using the given request, add the requested answers to the application and notify the user.
     * @param request the request to add the supervisor answer request with
     * @return the created request
     * @throws AccountNotExistsException if the supervisor does not exist
     * @throws ApplicationException if no application exists for the request
     * @throws InvalidStatusException if the application is not in the draft or referred state
     */
    AnswerRequest addAnswerRequest(AddAnswerRequest request) throws AccountNotExistsException, ApplicationException, InvalidStatusException;

    /**
     * Add the answers from the user to the application
     * @param request the request to add the answers to the application
     * @return true if successful, false if no request exists to update
     * @throws AccountNotExistsException if the supervisor does not exist
     * @throws InvalidStatusException if the application is not in the draft or referred state
     */
    boolean addRequestedAnswers(RespondAnswerRequest request) throws InvalidStatusException;

    /**
     * Get the request identified by the ID. If the application is no longer editable, this will return null
     * @param id the ID of the request
     * @return the request if found, or null if no longer valid or not found
     */
    AnswerRequest getRequest(Long id);

    /**
     * Get all the assigned requests for the given supervisor
     * @param supervisor the username of the supervisor
     * @return the list of answer requests from the supervisor
     */
    List<AnswerRequest> getRequests(String supervisor);
}
