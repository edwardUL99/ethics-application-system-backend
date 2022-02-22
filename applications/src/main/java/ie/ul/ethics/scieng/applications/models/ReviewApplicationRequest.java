package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * This class represents the request to either set an application to review or out of review
 */
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewApplicationRequest {
    /**
     * The application ID, not database ID
     */
    @NotNull
    private String id;
    /**
     * If true, the application will be set out of review
     */
    private boolean finishReview;
}
