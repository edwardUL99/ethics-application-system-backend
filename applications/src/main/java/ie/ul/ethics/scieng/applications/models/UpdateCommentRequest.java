package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This class represents a request to update/delete application comments. It shouldn't be used to add new comments
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UpdateCommentRequest {
    /**
     * The ID of the application
     */
    @NotNull
    private String id;
    /**
     * The updated comment. Expected to be a comment at the top level
     */
    @NotNull
    private ReviewSubmittedApplicationRequest.Comment updated;
    /**
     * Specifies if the comment should be deleted.
     */
    private boolean deleteComment;
}
