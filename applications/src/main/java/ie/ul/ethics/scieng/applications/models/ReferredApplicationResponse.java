package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;

import java.util.Set;

/**
 * This response represents a referred application
 */
public class ReferredApplicationResponse extends SubmittedApplicationResponse {
    /**
     * Create a response from the application
     *
     * @param application the application to create the response from
     */
    public ReferredApplicationResponse(Application application) {
        super(application);
    }

    /**
     * Validate that the application has the correct status for this response object
     *
     * @param application the application the response is being created from
     * @throws IllegalArgumentException if not valid
     */
    @Override
    protected void validateApplicationStatus(Application application) throws IllegalArgumentException {
        if (application.getStatus() != ApplicationStatus.REFERRED)
            throw new IllegalArgumentException("The application must be a ReferredApplication");
    }
}
