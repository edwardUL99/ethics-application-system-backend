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
         * The timestamp of when the application was created
         */
        private LocalDateTime createdAt;
    }
}
