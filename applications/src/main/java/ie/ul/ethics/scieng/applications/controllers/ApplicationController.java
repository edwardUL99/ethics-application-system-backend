package ie.ul.ethics.scieng.applications.controllers;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.exceptions.MappingException;
import ie.ul.ethics.scieng.applications.models.*;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.ids.ApplicationIDPolicy;
import ie.ul.ethics.scieng.applications.models.mapping.AcceptResubmittedRequest;
import ie.ul.ethics.scieng.applications.models.mapping.ApplicationRequestMapper;
import ie.ul.ethics.scieng.applications.models.mapping.MappedAcceptResubmittedRequest;
import ie.ul.ethics.scieng.applications.models.mapping.MappedApprovalRequest;
import ie.ul.ethics.scieng.applications.models.mapping.MappedReferApplicationRequest;
import ie.ul.ethics.scieng.applications.search.ApplicationSpecification;
import ie.ul.ethics.scieng.applications.search.DraftApplicationSpecification;
import ie.ul.ethics.scieng.applications.search.ReferredApplicationSpecification;
import ie.ul.ethics.scieng.applications.search.SubmittedApplicationSpecification;
import ie.ul.ethics.scieng.applications.services.ApplicationResponseService;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.common.search.SearchController;
import ie.ul.ethics.scieng.common.search.SearchCriteria;
import ie.ul.ethics.scieng.common.search.SearchException;
import ie.ul.ethics.scieng.common.search.SearchParser;
import ie.ul.ethics.scieng.common.search.SearchResponse;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ie.ul.ethics.scieng.common.Constants.*;

/**
 * This class represents the controller for the applications endpoints
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController implements SearchController<ApplicationResponse> {
    /**
     * The service for the applications processing
     */
    private final ApplicationService applicationService;
    /**
     * The request mapper for mapping requests
     */
    private final ApplicationRequestMapper requestMapper;
    /**
     * The user service for loading users
     */
    private final UserService userService;
    /**
     * The policy for generating application IDs
     */
    private final ApplicationIDPolicy applicationIDPolicy;
    /**
     * Authentication information to retrieve user's username
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;
    /**
     * A service to wrap application returning responses and cleans if necessary
     */
    private final ApplicationResponseService responseService;

    /**
     * Create the ApplicationController
     * @param applicationService the service for applications processing
     * @param requestMapper the mapper for mapping requests to entities
     * @param userService the user service for loading users
     * @param applicationIDPolicy the policy for generating IDs
     * @param responseService a service to wrap application returning responses and cleans if necessary
     */
    public ApplicationController(ApplicationService applicationService, ApplicationRequestMapper requestMapper, UserService userService,
                                 ApplicationIDPolicy applicationIDPolicy, ApplicationResponseService responseService) {
        this.applicationService = applicationService;
        this.requestMapper = requestMapper;
        this.userService = userService;
        this.applicationIDPolicy = applicationIDPolicy;
        this.responseService = responseService;
    }

    /**
     * This endpoint must be called before creating an application
     * @return the response body containing the ID
     */
    @GetMapping("/id")
    public ResponseEntity<?> generateId() {
        Map<String, Object> response = new HashMap<>();
        response.put("id", applicationIDPolicy.generate());

        return ResponseEntity.ok(response);
    }

    /**
     * This endpoint gets all the templates that are loaded in the system
     * @return the response body
     */
    @GetMapping("/templates")
    public ResponseEntity<?> getTemplates() {
        ApplicationTemplate[] templates = applicationService.getApplicationTemplates();
        ApplicationTemplateResponse response = new ApplicationTemplateResponse(templates);

        return ResponseEntity.ok(response);
    }

    /**
     * This endpoint retrieves a single template with the given ID
     * @param id the ID of the template
     * @return the response body
     */
    @GetMapping("/template")
    public ResponseEntity<?> getTemplate(@RequestParam Long id) {
        return Optional.ofNullable(applicationService.getApplicationTemplate(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * This endpoint retrieves the application with the given ID
     * @param id the id of the application
     * @param applicationId the applicationId if wants to be found by that
     * @param answerRequest true if the application is being retrieved in an answerRequest context, otherwise access will be blocked regardless of accessList
     * @return the response body
     */
    @GetMapping
    public ResponseEntity<?> getApplication(@RequestParam(required = false, name = "dbId") Long id,
                                            @RequestParam(required = false, name = "id") String applicationId,
                                            @RequestParam(required = false, name = "answerRequest") boolean answerRequest) {
        if ((id == null && applicationId == null) || (id != null && applicationId != null))
            return ResponseEntity.badRequest().build();

        Application application = (id == null) ? applicationService.getApplication(applicationId):applicationService.getApplication(id);

        if (application != null) {
            User user = userService.loadUser(authenticationInformation.getUsername());

            if (application.canBeViewedBy(user, answerRequest)) {
                return ResponseEntity.ok(ApplicationResponseFactory.buildResponse(application.clean(user)));
            } else {
                return respondError(INSUFFICIENT_PERMISSIONS);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This endpoint is used to retrieve all applications by user
     * @param viewable true to retrieve all viewable applications by this user, false to retrieve assigned applications
     * @return the response body
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserApplications(@RequestParam(required = false) boolean viewable) {
        try {
            String username = authenticationInformation.getUsername();
            User user = userService.loadUser(username);

            if (user == null) {
                return ResponseEntity.notFound().build();
            } else {
                List<ApplicationResponse> responses = ((viewable) ? applicationService.getViewableApplications(user):
                        applicationService.getAssignedApplications(user))
                        .stream()
                        .map(a -> a.clean(user))
                        .map(ApplicationResponseFactory::buildResponse)
                        .collect(Collectors.toList());

                return ResponseEntity.ok(responses);
            }
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return respondError(INSUFFICIENT_PERMISSIONS);
        }
    }

    /**
     * Verify that the username matches the authenticated username
     * @param username the username to verify
     * @return the response entity if not verified, null if verified
     */
    private ResponseEntity<?> verifyOwnUser(String username) {
        if (!username.equals(authenticationInformation.getUsername()))
            return respondError(INSUFFICIENT_PERMISSIONS);

        return null;
    }

    /**
     * The internal code to create the draft internally
     * @param request the request to create the draft
     * @return the response body
     */
    private ResponseEntity<?> createDraftApplicationInternal(CreateDraftApplicationRequest request) {
        Application draftApplication = requestMapper.createDraftRequestToDraft(request);
        draftApplication.setApplicationId(this.applicationIDPolicy.generate());

        if (draftApplication.getUser() == null) {
            return respondError(USER_NOT_FOUND);
        } else {
            Application application = applicationService.createApplication(draftApplication, false);
            return ResponseEntity.status(HttpStatus.CREATED).body(new CreateDraftApplicationResponse(application));
        }
    }

    /**
     * This endpoint is used to create a draft application
     * @param request the request to create the application
     * @return the response body
     */
    @PostMapping("/draft")
    public ResponseEntity<?> createDraftApplication(@RequestBody @Valid CreateDraftApplicationRequest request) {
        ResponseEntity<?> verification = verifyOwnUser(request.getUsername());

        if (verification != null)
            return verification;

        return createDraftApplicationInternal(request);
    }

    /**
     * Update the draft/referred application internally
     * @param application the draft application to update
     * @return the response body
     */
    private ResponseEntity<?> updateInternal(Application application) {
        try {
            applicationService.createApplication(application, true);

            UpdateDraftApplicationResponse response =
                    new UpdateDraftApplicationResponse(APPLICATION_UPDATED, application.getAnswers(), application.getLastUpdated(), application.getAttachedFiles());

            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            return respondError(ILLEGAL_UPDATE);
        }
    }

    /**
     * This endpoint allows a draft application to be updated
     * @param request the request to update the draft
     * @param servletRequest the request from the server
     * @return the response body
     */
    @PutMapping(value={"/draft", "/referred"})
    public ResponseEntity<?> updateDraftReferredApplication(@RequestBody @Valid UpdateDraftApplicationRequest request, HttpServletRequest servletRequest) {
        try {
            Application application =
                    (servletRequest.getRequestURI().contains("/draft")) ? requestMapper.updateDraftRequestToDraft(request):requestMapper.updateRequestToReferred(request);

            if (application != null) {
                application.setApplicationTemplate(request.getTemplate());
                ResponseEntity<?> verification = verifyOwnUser(application.getUser().getUsername());

                if (verification != null)
                    return verification;

                return updateInternal(application);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MappingException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }

    /**
     * A means to patch the answers provided on an application
     * @param request the request to patch the answers
     * @return the response body
     */
    @PatchMapping("/answers")
    public ResponseEntity<?> patchAnswers(@RequestBody PatchAnswersRequest request) {
        return responseService.process(v -> {
            Application application = applicationService.getApplication(request.getId());

            if (application == null) {
                return null;
            } else {
                return applicationService.patchAnswers(application, request.getAnswers());
            }
        });
    }

    /**
     * This endpoint represents the submission point for a draft or referred application
     * @param request the submission request
     * @return the response body
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitApplication(@RequestBody @Valid SubmitApplicationRequest request) {
        try {
            return responseService.process(v -> {
                Application application = requestMapper.submitRequestToApplication(request);

                if (application != null) {
                    ResponseEntity<?> verification = verifyOwnUser(application.getUser().getUsername());

                    if (verification != null)
                        throw new ApplicationResponseService.TaskInterrupt(verification);

                    return applicationService.submitApplication(application);
                } else {
                    return null;
                }
            });
        } catch (MappingException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }

    /**
     * Assigns the provided committee members to the application
     * @param request the request to assign committee members
     * @return the response body
     */
    @PostMapping("/assign")
    public ResponseEntity<?> assignCommitteeMember(@RequestBody @Valid AssignReviewerRequest request) {
        Application application = this.applicationService.getApplication(request.getId());

        if (application == null) {
            return ResponseEntity.notFound().build();
        } else {
            List<User> members = request.getMembers().stream()
                    .map(this.userService::loadUser)
                    .collect(Collectors.toList());

            if (members.stream().anyMatch(Objects::isNull)) {
                return ResponseEntity.notFound().build();
            } else {
                try {
                    Application assigned = this.applicationService.assignCommitteeMembers(application, members);
                    return ResponseEntity.ok(new AssignMembersResponse(assigned));
                } catch (InvalidStatusException ex) {
                    ex.printStackTrace();
                    return respondError(INVALID_APPLICATION_STATUS);
                } catch (ApplicationException ex) {
                    ex.printStackTrace();

                    if (ex.getMessage().equals(ApplicationService.CANT_REVIEW)) {
                        return respondError(INSUFFICIENT_PERMISSIONS);
                    } else {
                        return respondError(ex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * The endpoint for unassigning a committee member from the application
     * @param username the username of the committee member to remove
     * @param id the ID of the application
     * @return the response body
     */
    @PostMapping("/unassign/{username}")
    public ResponseEntity<?> unassignCommitteeMember(@PathVariable String username, @RequestParam String id) {
        try {
            return responseService.process(v -> {
                Application application = this.applicationService.getApplication(id);

                if (application == null) {
                    return null;
                } else {
                    return this.applicationService.unassignCommitteeMember(application, username);
                }
            });
        } catch (InvalidStatusException ex) {
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }

    /**
     * The endpoint to accept a re-submitted application
     * @param request the request to accept the application and assign it to committee members
     * @return the response body
     */
    @PostMapping("/resubmit")
    public ResponseEntity<?> resubmitApplication(@RequestBody @Valid AcceptResubmittedRequest request) {
        try {
            return responseService.process(v -> {
                MappedAcceptResubmittedRequest mapped = requestMapper.mapAcceptResubmittedRequest(request);
                Application application = mapped.getApplication();
                List<User> committeeMembers = mapped.getCommitteeMembers();

                if (application == null || committeeMembers.stream().anyMatch(Objects::isNull)) {
                    return null;
                } else {
                    return applicationService.acceptResubmitted(application, committeeMembers);
                }
            });
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return respondError(ex.getMessage());
        }
    }

    /**
     * This endpoint allows the review status to be set on an application
     * @param request the request to set the review status
     * @return the response body
     */
    @PostMapping("/review")
    public ResponseEntity<?> reviewApplication(@RequestBody @Valid ReviewApplicationRequest request) {
        try {
            return responseService.process(v -> {
                Application application = applicationService.getApplication(request.getId());

                if (application == null) {
                    return null;
                } else {
                    return applicationService.reviewApplication(application, request.isFinishReview());
                }
            });
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }

    /**
     * This endpoint allows a reviewer to update a submitted application in review by adding comments to it
     * @param request the request to review the application
     * @return the response body
     */
    @PutMapping("/review")
    public ResponseEntity<?> reviewApplication(@RequestBody @Valid ReviewSubmittedApplicationRequest request) {
        try {
            return responseService.process(v -> {
                Application mapped = requestMapper.reviewSubmittedRequestToSubmitted(request);

                if (mapped == null) {
                    return null;
                } else {
                    return applicationService.createApplication(mapped, true);
                }
            });
        } catch (MappingException ex) {
            ex.printStackTrace();
            return respondError(USER_NOT_FOUND);
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }

    /**
     * Update the comments of the request
     * @param request the request to update comments with
     * @return the response body
     */
    @PatchMapping("/comment")
    public ResponseEntity<?> patchComment(@RequestBody @Valid UpdateCommentRequest request) {
        try {
            return responseService.process(v -> {
                Application loaded = applicationService.getApplication(request.getId());

                if (loaded == null) {
                    return null;
                } else {
                    return applicationService.patchComment(loaded, requestMapper.mapComment(request.getUpdated()), request.isDeleteComment());
                }
            });
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Finish the review for a particular assigned committee member
     * @param request the request to mark the committee member as finished reviewing
     * @return the response body
     */
    @PostMapping("/review/finish")
    public ResponseEntity<?> finishReview(@RequestBody @Valid FinishReviewRequest request) {
        try {
            return responseService.process(v -> {
                Application application = this.applicationService.getApplication(request.getId());

                if (application == null) {
                    return null;
                } else {
                    return applicationService.markMemberReviewComplete(application, request.getMember());
                }
            });
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }

    /**
     * This endpoint allows an application to be approved/rejected
     * @param request the request to approve/reject the application
     * @return the response body
     */
    @PostMapping("/approve")
    public ResponseEntity<?> approveApplication(@RequestBody @Valid ApproveApplicationRequest request) {
        try {
            return responseService.process(v -> {
                MappedApprovalRequest mapped = requestMapper.mapApprovalRequest(request);
                Application application = mapped.getApplication();
                Comment finalComment;

                if (application == null || ((finalComment = mapped.getFinalComment()) != null && finalComment.getUser() == null)) {
                    return null;
                } else {
                    return applicationService.approveApplication(application, mapped.isApprove(), finalComment);
                }
            });
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        }
    }

    /**
     * This endpoint is used to refer applications
     * @param request the request to refer the application
     * @return the response body
     */
    @PostMapping("/refer")
    public ResponseEntity<?> referApplication(@RequestBody @Valid ReferApplicationRequest request) {
        try {
            return responseService.process(v -> {
                MappedReferApplicationRequest mapped = requestMapper.mapReferApplicationRequest(request);
                Application application = mapped.getApplication();
                User referrer = mapped.getReferrer();

                if (application == null || referrer == null) {
                    return null;
                } else {
                    return applicationService.referApplication(application, mapped.getEditableFields(), referrer);
                }
            });
        } catch (InvalidStatusException ex) {
            ex.printStackTrace();
            return respondError(INVALID_APPLICATION_STATUS);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            return respondError(ex.getMessage());
        }
    }

    /**
     * This endpoint deletes the user's own application if in draft state
     * @param id the ID of the application
     * @return the response body
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteApplication(@RequestParam String id) {
        Application application = applicationService.getApplication(id);

        if (application == null) {
            return ResponseEntity.notFound().build();
        } else {
            ResponseEntity<?> verification = verifyOwnUser(application.getUser().getUsername());

            if (verification != null)
                return verification;

            if (application.getStatus() != ApplicationStatus.DRAFT)
                return respondError(INVALID_APPLICATION_STATUS);

            applicationService.deleteApplication(application);

            return ResponseEntity.ok().build();
        }
    }

    /**
     * This endpoint allows an admin delete any application in any status
     * @param id the ID of the application
     * @return the response body
     */
    @DeleteMapping("/admin/delete")
    public ResponseEntity<?> deleteApplicationAdmin(@RequestParam String id) {
        Application application = applicationService.getApplication(id);

        if (application == null) {
            return ResponseEntity.notFound().build();
        } else {
            applicationService.deleteApplication(application);

            return ResponseEntity.ok().build();
        }
    }

    /**
     * Get the applications with committee members assigned with the username in the query
     * @param query the query to parse (only the first part with assigned will be parsed)
     * @return the list of found applications
     */
    private List<Application> getApplicationsWithUserAssigned(String query) {
        query += ",";

        Pattern pattern = Pattern.compile("(assigned)(:=)(.+?),");
        Matcher matcher = pattern.matcher(query);

        if (matcher.find()) {
            SearchCriteria criteria = new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3), false);

            if (!criteria.getOperation().equals(":=")) {
                return null;
            } else {
                String username = (String)criteria.getValue();
                User user = this.userService.loadUser(username);

                if (user != null) {
                    return applicationService.getAssignedApplications(user);
                } else {
                    return Collections.emptyList();
                }
            }
        }

        return Collections.emptyList();
    }

    /**
     * Search through applications until you hit the first instance that the field belongs to.
     * This "hack" is required as each class of the Application inheritance hierarchy needs to be searched through for the field.
     * First it searches with the generic ApplicationSpecification for any fields shared between all, then in the order of
     * DraftApplicationSpecification (maybe redundant since no extra fields), SubmittedApplicationSpecification and finally ReferredApplicationSpecification
     * @param query the query to create the specification from
     * @param or true to or multiple queries, false to and them
     * @return the list of found applications
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<Application> findApplications(String query, boolean or) {
        if (query.contains("assigned")) {
            return this.getApplicationsWithUserAssigned(query);
        } else {
            List<Class<? extends ApplicationSpecification>> specClasses =
                    List.of(ApplicationSpecification.class, DraftApplicationSpecification.class, SubmittedApplicationSpecification.class, ReferredApplicationSpecification.class);

            for (Class<? extends ApplicationSpecification> spec : specClasses) {
                try {
                    Specification<Application> specification =
                            new SearchParser<>(spec).parse(query, ApplicationSpecification.OPERATION_PATTERN, or);

                    List<Application> searched = this.applicationService.search(specification);

                    if (searched.size() > 0)
                        return searched;
                } catch (SearchException ignored) {
                } catch (Exception ex) {
                    throw new SearchException(null, ex);
                }
            }

            return Collections.emptyList();
        }
    }

    /**
     * Searches for applications with the given search query
     * @param query the search query
     * @param or true to or multiple queries or false to and
     * @return the list of found applications
     */
    @GetMapping("/search")
    public ResponseEntity<SearchResponse<ApplicationResponse>> search(@RequestParam String query, @RequestParam(required = false) boolean or) {
        try {
            User user = userService.loadUser(authenticationInformation.getUsername());

            List<Application> found = this.findApplications(query, or);

            List<ApplicationResponse> responses = found
                    .stream()
                    .filter(a -> a.canBeViewedBy(user))
                    .map(a -> {
                        a.clean(user);

                        return ApplicationResponseFactory.buildResponse(a);
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new SearchResponse<>(responses, null));
        } catch (SearchException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(new SearchResponse<>(List.of(), SEARCH_FAILED));
        }
    }
}
