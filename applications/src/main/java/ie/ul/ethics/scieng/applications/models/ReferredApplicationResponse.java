package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.annotations.ApplicationResponseRegistration;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This response represents a referred application
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ApplicationResponseRegistration(status = ApplicationStatus.REFERRED)
public class ReferredApplicationResponse extends SubmittedApplicationResponse {
    /**
     * The list of field component IDs that can be edited
     */
    private List<String> editableFields;
    /**
     * The username of the user that referred the application
     */
    private String referredBy;

    /**
     * Create a response from the application
     *
     * @param application the application to create the response from
     */
    public ReferredApplicationResponse(Application application) {
        super(application);
        this.editableFields = application.getEditableFields();
        this.referredBy = application.getReferredBy().getUsername();
    }

    /**
     * Validate that the application has the correct status for this response object
     *
     * @param application the application the response is being created from
     * @throws InvalidStatusException if not valid
     */
    @Override
    protected void validateApplicationStatus(Application application) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.REFERRED)
            throw new InvalidStatusException("The application must be a ReferredApplication");
    }
}
