package ie.ul.ethics.scieng.applications.controllers;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AddAnswerRequest;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AddAnswerRequestResponse;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AnswerRequestResponse;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.RespondAnswerRequest;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AnswerRequest;
import ie.ul.ethics.scieng.applications.services.AnswerRequestService;
import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;
import static ie.ul.ethics.scieng.common.Constants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * This controller provides the endpoints for when a supervisor is requested to give inputs on the application.
 * It provides all endpoints under the same /api/applications base for consistency
 */
@RestController
@RequestMapping("/api/applications")
public class AnswerRequestController {
    /**
     * The service for performing supervisor requests
     */
    private final AnswerRequestService requestService;

    /**
     * Construct an instance
     * @param requestService the service for performing supervisor requests
     */
    @Autowired
    public AnswerRequestController(AnswerRequestService requestService) {
        this.requestService = requestService;
    }

    /**
     * This endpoint gets all the requests for the provided username
     * @param username the username of the user to retrieve requests for
     * @return the response body
     */
    @GetMapping("/answers/requests")
    public ResponseEntity<?> getRequests(@RequestParam String username) {
        return ResponseEntity.ok(requestService.getRequests(username)
                .stream()
                .map(AnswerRequestResponse::new)
                .collect(Collectors.toList()));
    }

    /**
     * This endpoint retrieves the request identified by the given ID
     * @param id the ID of the request to retrieve
     * @return the response body
     */
    @GetMapping("/answers/request")
    public ResponseEntity<?> getRequest(@RequestParam Long id) {
        AnswerRequest request = requestService.getRequest(id);

        if (request == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(new AnswerRequestResponse(request));
        }
    }

    /**
     * This endpoint creates a supervisor answer request
     * @param request the request to get an answer from the supervisor
     * @return the response body
     */
    @PostMapping("/answers/request")
    public ResponseEntity<?> createRequest(@RequestBody @Valid AddAnswerRequest request) {
        try {
            AnswerRequest answerRequest = requestService.addAnswerRequest(request);

            return ResponseEntity.ok(new AddAnswerRequestResponse(answerRequest));
        } catch (AccountNotExistsException ex) {
            ex.printStackTrace();
            return respondError(ACCOUNT_NOT_EXISTS);
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This endpoint accepts answers made to the components by the supervisor
     * @param request the request to answer the requested fields
     * @return the response body
     */
    @PostMapping("/answers/answer")
    public ResponseEntity<?> answerRequest(@RequestBody @Valid RespondAnswerRequest request) {
        try {
            if (requestService.addRequestedAnswers(request)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }
}
