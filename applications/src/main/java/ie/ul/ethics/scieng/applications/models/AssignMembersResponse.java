package ie.ul.ethics.scieng.applications.models;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.users.models.UserResponseShortened;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents a response from assigning a member to the application
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AssignMembersResponse {
    /**
     * The ID of the application the members were assigned to
     */
    private String id;
    /**
     * The members assigned to the application
     */
    private List<AssignedCommitteeMember> members;
    /**
     * The timestamp of when the application was last updated
     */
    private LocalDateTime lastUpdated;

    /**
     * Create a response from the given application
     * @param application the application to create the response from
     */
    public AssignMembersResponse(Application application) {
        this.id = application.getApplicationId();
        this.members = application.getAssignedCommitteeMembers().stream()
                .map(AssignedCommitteeMember::new)
                .collect(Collectors.toList());
        this.lastUpdated = application.getLastUpdated();
    }

    /**
     * This class represents a response AssignedCommitteeMember
     */
    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssignedCommitteeMember {
        /**
         * The database ID
         */
        private Long id;
        /**
         * The ID of the application the member is assigned to
         */
        private String applicationId;
        /**
         * The member assigned
         */
        private UserResponseShortened member;
        /**
         * Determines if the committee member has finished their review
         */
        private boolean finishReview;

        /**
         * Create the response object from the provided entity
         * @param member the entity to convert to the response
         */
        public AssignedCommitteeMember(ie.ul.ethics.scieng.applications.models.applications.AssignedCommitteeMember member) {
            this.id = member.getId();
            this.applicationId = member.getApplicationId();
            this.member = new UserResponseShortened(member.getUser());
            this.finishReview = member.isFinishReview();
        }
    }
}
