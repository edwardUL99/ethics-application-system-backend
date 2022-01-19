package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.UpdateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.users.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This provides the default implementation for the mapper
 */
@Component
public class ApplicationRequestMapperImpl implements ApplicationRequestMapper {
    /**
     * The user service to help with mapping
     */
    private final UserService userService;
    /**
     * The application service to help with mapping
     */
    private final ApplicationService applicationService;

    /**
     * Create an ApplicationRequestMapperImpl
     * @param userService the user service to help with mapping
     * @param applicationService the application service to help with mapping
     */
    @Autowired
    public ApplicationRequestMapperImpl(UserService userService, ApplicationService applicationService) {
        this.userService = userService;
        this.applicationService = applicationService;
    }

    /**
     * Maps the create draft request to the draft application
     *
     * @param request the request to map
     * @return the mapped draft application
     */
    @Override
    public DraftApplication createDraftRequestToDraft(CreateDraftApplicationRequest request) {
        return new DraftApplication(null, null, userService.loadUser(request.getUsername()), request.getApplicationTemplate(),
                request.getValues());
    }

    /**
     * Maps the update draft request to the draft application
     *
     * @param request the request to map
     * @return the mapped draft application
     */
    @Override
    public DraftApplication updateDraftRequestToDraft(UpdateDraftApplicationRequest request) {
        Long id = request.getId();
        Application loaded = applicationService.getApplication(id);

        if (loaded != null) {
            if (loaded.getStatus() != ApplicationStatus.DRAFT)
                throw new IllegalStateException("The application with ID " + id + " is not a DraftApplication");

            DraftApplication draftApplication = (DraftApplication) loaded;
            draftApplication.setAnswers(request.getValues());

            return draftApplication;
        } else {
            return null;
        }
    }
}