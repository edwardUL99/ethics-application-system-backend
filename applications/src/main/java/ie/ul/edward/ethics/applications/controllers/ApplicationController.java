package ie.ul.edward.ethics.applications.controllers;

import ie.ul.edward.ethics.applications.models.ApplicationTemplateResponse;
import ie.ul.edward.ethics.applications.models.CreateDraftApplicationRequest;
import ie.ul.edward.ethics.applications.models.CreateDraftApplicationResponse;
import ie.ul.edward.ethics.applications.models.UpdateDraftApplicationRequest;
import ie.ul.edward.ethics.applications.models.applications.Application;
import ie.ul.edward.ethics.applications.models.applications.DraftApplication;
import ie.ul.edward.ethics.applications.models.mapping.ApplicationRequestMapper;
import ie.ul.edward.ethics.applications.services.ApplicationService;
import ie.ul.edward.ethics.applications.templates.ApplicationTemplate;

import ie.ul.edward.ethics.authentication.jwt.AuthenticationInformation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static ie.ul.edward.ethics.common.Constants.*;

/**
 * This class represents the controller for the applications endpoints
 * TODO add /api/applications/any/ get which allows retrieving all applications by id, lock with VIEW_ALL_APPLICATIONS permissions. In /api/applications/, if the user of the loaded application doesn't match the username of the authenticated username, throw insufficient permissions
 * or if username isn't equal to this username, check if the user has VIEW_ALL_APPLICATIONS permission and then retrieve it
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
    /**
     * The service for the applications processing
     */
    private final ApplicationService applicationService;
    /**
     * The request mapper for mapping requests
     */
    private final ApplicationRequestMapper requestMapper;
    /**
     * Authentication information to retrieve user's username
     */
    @Resource(name = "authenticationInformation")
    private AuthenticationInformation authenticationInformation;

    /**
     * Create the ApplicationController
     * @param applicationService the service for applications processing
     * @param requestMapper the mapper for mapping requests to entities
     */
    public ApplicationController(ApplicationService applicationService, ApplicationRequestMapper requestMapper) {
        this.applicationService = applicationService;
        this.requestMapper = requestMapper;
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
     * This endpoint retrieves the application with the given ID
     * @param id the id of the application
     * @return the response body
     */
    @GetMapping
    public ResponseEntity<?> getApplication(@RequestParam Long id) {
        Optional<Application> optional = Optional.ofNullable(applicationService.getApplication(id));

        return optional.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
    private ResponseEntity<?> createDraftInternal(CreateDraftApplicationRequest request) {
        DraftApplication draftApplication = requestMapper.createDraftRequestToDraft(request);

        if (draftApplication.getUser() == null) {
            return respondError(USER_NOT_FOUND);
        } else {
            Application application = applicationService.createDraftApplication(draftApplication, false);

            return ResponseEntity.status(HttpStatus.CREATED).body(new CreateDraftApplicationResponse((DraftApplication) application));
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

        return createDraftInternal(request);
    }

    /**
     * Update the draft application internally
     * @param draftApplication the draft application to update
     * @return the response body
     */
    private ResponseEntity<?> updateDraftInternal(DraftApplication draftApplication) {
        try {
            Map<String, Object> response = new HashMap<>();
            applicationService.createDraftApplication(draftApplication, true);
            response.put(MESSAGE, APPLICATION_UPDATED);

            return ResponseEntity.ok(response);
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            return respondError(ILLEGAL_UPDATE);
        }
    }

    /**
     * This endpoint allows a draft application to be updated
     * @param request the request to update the draft
     * @return the response body
     */
    @PutMapping("/draft")
    public ResponseEntity<?> updateDraftApplication(@RequestBody @Valid UpdateDraftApplicationRequest request) {
        try {
            DraftApplication draftApplication = requestMapper.updateDraftRequestToDraft(request);

            if (draftApplication != null) {
                ResponseEntity<?> verification = verifyOwnUser(draftApplication.getUser().getUsername());

                if (verification != null)
                    return verification;

                return updateDraftInternal(draftApplication);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            return respondError(APPLICATION_NOT_DRAFT);
        }
    }
}
