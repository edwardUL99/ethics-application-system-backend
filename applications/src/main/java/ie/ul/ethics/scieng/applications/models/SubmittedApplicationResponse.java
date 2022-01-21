package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

/**
 * This class represents a response for a submitted application
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class SubmittedApplicationResponse extends ApplicationResponse {
    /**
     * The comments left on the submitted application
     */
    private Map<String, Comment> comments;

    /**
     * Create a response from the application
     *
     * @param application the application to create the response from
     */
    public SubmittedApplicationResponse(Application application) {
        super(application);
        validateApplicationStatus(application);
    }

    /**
     * Validate that the application has the correct status for this response object
     *
     * @param application the application the response is being created from
     * @throws IllegalArgumentException if not valid
     */
    @Override
    protected void validateApplicationStatus(Application application) throws IllegalArgumentException {
        ApplicationStatus status = application.getStatus();
        Set<ApplicationStatus> permissible = Set.of(ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEW,
                ApplicationStatus.REVIEWED, ApplicationStatus.APPROVED, ApplicationStatus.REJECTED);

        if (!permissible.contains(status))
            throw new IllegalArgumentException("The application must have one of the following states: " + permissible);
    }
}
