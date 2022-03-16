package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.annotations.ApplicationResponseRegistration;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.users.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This response class represents an application that has been resubmitted
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ApplicationResponseRegistration(status = ApplicationStatus.RESUBMITTED)
public class ResubmittedApplicationResponse extends SubmittedApplicationResponse {
    /**
     * The list of previous committee members
     */
    private List<String> previousCommitteeMembers;

    /**
     * Create a response from the application
     *
     * @param application the application to create the response from
     */
    public ResubmittedApplicationResponse(Application application) {
        super(application);
        previousCommitteeMembers = application.getPreviousCommitteeMembers()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
    }

    /**
     * Validate that the application has the correct status for this response object
     *
     * @param application the application the response is being created from
     * @throws InvalidStatusException if not valid
     */
    @Override
    protected void validateApplicationStatus(Application application) throws InvalidStatusException {
        if (application.getStatus() != ApplicationStatus.RESUBMITTED)
            throw new InvalidStatusException("The application must be in a " + ApplicationStatus.RESUBMITTED + " state for this response object");
    }
}
