package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This class represents the request to approve/reject an application
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ApproveApplicationRequest {
    /**
     * The application ID (not dbId) of the application to approve
     */
    @NotNull
    private String id;
    /**
     * True to approve the application, false to not approve it
     */
    @NotNull
    private boolean approve;
    /**
     * The final comment to leave on the application after approving it
     */
    private ReviewSubmittedApplicationRequest.Comment finalComment;
}
