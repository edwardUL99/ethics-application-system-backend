package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.annotations.ApplicationResponseRegistration;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.users.models.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class represents a response for a submitted application
 */
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ApplicationResponseRegistration(status = {
        ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEW, ApplicationStatus.REVIEWED,
        ApplicationStatus.APPROVED, ApplicationStatus.REJECTED
}, applicationClass = SubmittedApplication.class)
public class SubmittedApplicationResponse extends ApplicationResponse {
    /**
     * The comments left on the submitted application
     */
    private Map<String, Comment> comments;
    /**
     * The list of usernames of the assigned committee members
     */
    private List<String> assignedCommitteeMembers;
    /**
     * The final comment left on the application if it is approved/rejected
     */
    private Comment finalComment;

    /**
     * Create a response from the application
     *
     * @param application the application to create the response from
     */
    public SubmittedApplicationResponse(SubmittedApplication application) {
        super(application);

        this.comments = application.getComments();
        this.assignedCommitteeMembers = application.getAssignedCommitteeMembers()
                .stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        this.finalComment = application.getFinalComment();
    }

    /**
     * Validate that the application has the correct status for this response object
     *
     * @param application the application the response is being created from
     * @throws InvalidStatusException if not valid
     */
    @Override
    protected void validateApplicationStatus(Application application) throws InvalidStatusException {
        ApplicationStatus status = application.getStatus();
        Set<ApplicationStatus> permissible = Set.of(ApplicationStatus.SUBMITTED, ApplicationStatus.REVIEW,
                ApplicationStatus.REVIEWED, ApplicationStatus.APPROVED, ApplicationStatus.REJECTED);

        if (!permissible.contains(status))
            throw new InvalidStatusException("The application must have one of the following states: " + permissible);
    }
}
