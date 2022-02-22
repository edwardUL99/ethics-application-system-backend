package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a request to finish a review on an application
 */
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FinishReviewRequest {
    /**
     * The ID of the application
     */
    private String id;
    /**
     * The username of the committee member
     */
    private String member;
}
