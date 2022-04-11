package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.email.ApplicationsEmailService;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AddAnswerRequest;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.RespondAnswerRequest;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AnswerRequest;
import ie.ul.ethics.scieng.applications.repositories.AnswerRequestRepository;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.users.exceptions.AccountNotExistsException;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the default implementation of the AnswerRequestService
 */
@Service
public class AnswerRequestServiceImpl implements AnswerRequestService {
    /**
     * The service for loading the supervisor accounts
     */
    private final UserService userService;
    /**
     * The service for loading applications
     */
    private final ApplicationService applicationService;
    /**
     * The repository for saving the supervisor answer requests
     */
    private final AnswerRequestRepository repository;
    /**
     * The service for sending e-mail notifications
     */
    private final ApplicationsEmailService emailService;

    /**
     * The statuses that are valid for answer requests
     */
    private static final Set<ApplicationStatus> validStates = Set.of(ApplicationStatus.DRAFT, ApplicationStatus.REFERRED);

    /**
     * Create an instance with the given dependencies
     * @param userService the user service for loading the supervisor accounts
     * @param applicationService the service for loading and updating applications
     * @param repository the repository for storing/retrieving the requests with
     */
    @Autowired
    public AnswerRequestServiceImpl(UserService userService, ApplicationService applicationService,
                                    AnswerRequestRepository repository, ApplicationsEmailService emailService) {
        this.userService = userService;
        this.applicationService = applicationService;
        this.repository = repository;
        this.emailService = emailService;
    }

    /**
     * Get the application and verify it is not null and is in the correct state
     * @param id the ID of the application
     * @return the application loaded
     */
    private Application getAndVerifyApplication(String id) {
        Application application = applicationService.getApplication(id);

        return verifyApplication(application);
    }

    /**
     * Verify that the application is not null and in the correct status
     * @param application the application to verify
     * @return the verified application
     */
    private Application verifyApplication(Application application) {
        if (application == null) {
            throw new ApplicationException("The application for this request does not exist");
        } else if (!validStates.contains(application.getStatus())) {
            throw new InvalidStatusException("The application must be in DRAFT or REFERRED status to perform this action");
        } else {
            return application;
        }
    }

    /**
     * Get the supervisor and verify that they are not null
     * @param username the username of the supervisor
     * @return the user object for the supervisor
     */
    private User getAndVerifySupervisor(String username) {
        User supervisor = userService.loadUser(username);

        if (supervisor == null) {
            throw new AccountNotExistsException("The supervisor user " + username + " does not exist");
        } else {
            return supervisor;
        }
    }

    /**
     * Nullify all the database IDs in the application components
     * @param components the components to clear IDs from
     */
    private void nullifyComponents(List<ApplicationComponent> components) {
        components.forEach(ApplicationComponent::clearDatabaseIDs);
    }

    /**
     * Using the given request, add the requested answers to the application and notify the supervisor.
     *
     * @param request the request to add the supervisor answer request with
     * @return the created request
     * @throws AccountNotExistsException if the supervisor does not exist
     * @throws ApplicationException      if no application exists for the request
     * @throws InvalidStatusException    if the application is not in the draft or referred state
     */
    @Override
    public AnswerRequest addAnswerRequest(AddAnswerRequest request) throws AccountNotExistsException, ApplicationException, InvalidStatusException {
        Application application = getAndVerifyApplication(request.getId());
        User user = getAndVerifySupervisor(request.getUsername());
        List<ApplicationComponent> components = request.getComponents();
        nullifyComponents(components);
        AnswerRequest newRequest = repository.save(new AnswerRequest(null, application, user, components, LocalDateTime.now()));

        application.grantUserAccess(user);
        applicationService.createApplication(application, true);

        emailService.addAnswerInputRequested(newRequest);

        return newRequest;
    }

    /**
     * Add the answers from the supervisor to the application
     *
     * @param request the request to add the answers to the application
     * @return true if successful, false if no request exists to update
     * @throws AccountNotExistsException if the supervisor does not exist
     * @throws InvalidStatusException    if the application is not in the draft or referred state
     */
    @Override
    public boolean addRequestedAnswers(RespondAnswerRequest request) throws ApplicationException, InvalidStatusException {
        AnswerRequest answerRequest = repository.findById(request.getRequestId()).orElse(null);

        if (answerRequest == null) {
            return false;
        } else {
            Application application = answerRequest.getApplication();
            User supervisor = answerRequest.getUser();
            Map<String, Answer> answers = request.getAnswers();
            Map<String, Answer> applicationAnswers = application.getAnswers();

            answers.forEach((k, v) -> {
                v.setUser(supervisor);
                Answer answer = applicationAnswers.get((k));

                if (answer == null || answer.getValue().isEmpty())
                    // only replace the answer if one is not already provided
                    applicationAnswers.put(k, v);
            });

            repository.delete(answerRequest);
            applicationService.createApplication(application, true);
            emailService.sendAnsweredResponse(answerRequest);

            return true;
        }
    }

    /**
     * Get the request identified by the ID. If the application is no longer editable, this will return null
     *
     * @param id the ID of the request
     * @return the request if found, or null if no longer valid or not found
     */
    @Override
    public AnswerRequest getRequest(Long id) {
        return repository.findById(id).orElse(null);
    }

    /**
     * Get all the assigned requests for the given supervisor
     *
     * @param supervisor the username of the supervisor
     * @return the list of answer requests from the supervisor
     */
    @Override
    public List<AnswerRequest> getRequests(String supervisor) {
        List<AnswerRequest> requests = repository.findByUser_username(supervisor);
        List<AnswerRequest> returned = new ArrayList<>(requests);

        requests.forEach(r -> {
            if (!validStates.contains(r.getApplication().getStatus())) {
                repository.delete(r);
                returned.remove(r);
            }
        });

        return returned;
    }
}
