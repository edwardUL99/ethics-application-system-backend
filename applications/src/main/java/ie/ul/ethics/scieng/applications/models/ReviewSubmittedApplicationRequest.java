package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents a request for a reviewer to add comments to a submitted application
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ReviewSubmittedApplicationRequest {
    /**
     * The id of the application (not the db ID)
     */
    private String id;
    /**
     * The list of comments to add
     */
    private List<Comment> comments;

    /**
     * This comment represents a simplified version of the Comment entity to be mapped
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class Comment {
        /**
         * The database ID of the comment if any
         */
        private Long id;
        /**
         * The username of the user that left the comment
         */
        private String username;
        /**
         * The comment text
         */
        private String comment;
        /**
         * The ID of the component
         */
        private String componentId;
        /**
         * Sub-comments left on the comment
         */
        private List<Comment> subComments;
        /**
         * Determines if the comment is shared with applicants
         */
        private boolean sharedApplicant;
        /**
         * Determines if the comment is shared with all reviewers or just admin/chair
         */
        private boolean sharedReviewer;
        /**
         * The timestamp of when the application was created
         */
        private LocalDateTime createdAt;
        /**
         * Determines if the comment has been edited or not
         */
        private boolean edited;

        /**
         * Create a non-shared comment
         * @param id the database ID
         * @param username username of user creating the comment
         * @param comment the comment content
         * @param componentId the id of the component the comment is attached to
         * @param subComments the list of sub-comments
         * @param createdAt timestamp of when the application was created
         */
        public Comment(Long id, String username, String comment, String componentId, List<Comment> subComments,
                       LocalDateTime createdAt) {
            this(id, username, comment, componentId, subComments, false, false, createdAt, false);
        }
    }
}
