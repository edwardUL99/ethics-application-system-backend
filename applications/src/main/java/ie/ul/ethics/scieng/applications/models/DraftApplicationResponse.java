package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.annotations.ApplicationResponseRegistration;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This response represents a response for draft applications
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ApplicationResponseRegistration(status = ApplicationStatus.DRAFT, applicationClass = DraftApplication.class)
public class DraftApplicationResponse extends ApplicationResponse {
    /**
     * Create a response from the application
     *
     * @param application the application to create the response from
     */
    public DraftApplicationResponse(DraftApplication application) {
        super(application);
    }

    /**
     * Validate that the application has the correct status for this response object
     *
     * @param application the application the response is being created from
     * @throws InvalidStatusException if not valid
     */
    @Override
    protected void validateApplicationStatus(Application application) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.DRAFT)
            throw new InvalidStatusException("The provided application must be a DraftApplication");
    }
}
