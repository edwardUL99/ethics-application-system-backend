package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.annotations.ApplicationResponseRegistration;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationComments;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
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
})
public class SubmittedApplicationResponse extends ApplicationResponse {
    /**
     * The comments left on the submitted application
     */
    private Map<String, ApplicationComments> comments;
    /**
     * The list of usernames of the assigned committee members
     */
    private List<AssignedCommitteeMemberResponse> assignedCommitteeMembers;
    /**
     * The final comment left on the application if it is approved/rejected
     */
    private Comment finalComment;
    /**
     * The timestamp of when the application was submitted
     */
    private LocalDateTime submittedTime;
    /**
     * The timestamp of when the application was approved/rejected
     */
    private LocalDateTime approvalTime;

    /**
     * Create a response from the application
     *
     * @param application the application to create the response from
     */
    public SubmittedApplicationResponse(Application application) {
        super(application);

        this.comments = application.getComments();

        this.comments.values().forEach(v -> {
            if (v.getId() == null) {
                System.out.println("Null ID: " + v);
            }

            v.getComments().forEach(c -> {
                if (c.getId() == null)
                    System.out.println("Null comment ID: " + c);
            });
        });

        this.assignedCommitteeMembers = application.getAssignedCommitteeMembers()
                .stream()
                .map(u -> new AssignedCommitteeMemberResponse(u.getId(), u.getApplicationId(), u.getUser().getUsername(), u.isFinishReview()))
                .collect(Collectors.toList());
        this.finalComment = application.getFinalComment();
        this.submittedTime = application.getSubmittedTime();
        this.approvalTime = application.getApprovalTime();
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

    /**
     * The response for an assigned committee member
     */
    @EqualsAndHashCode
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssignedCommitteeMemberResponse {
        /**
         * The database ID
         */
        private Long id;
        /**
         * The ID of the application the member is assigned to
         */
        private String applicationId;
        /**
         * The username of the committee member
         */
        private String username;
        /**
         * Determines if the committee member has finished their review
         */
        private boolean finishReview;
    }
}
