package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This request represents the submission of a draft application
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubmitApplicationRequest {
    /**
     * The application ID (not database ID) of the application being submitted
     */
    private String id;
}
