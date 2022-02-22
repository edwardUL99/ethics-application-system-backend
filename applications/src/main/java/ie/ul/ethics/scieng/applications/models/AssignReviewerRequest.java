package ie.ul.ethics.scieng.applications.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * This class represents a request to assign committee members to the application
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AssignReviewerRequest {
    /**
     * The ID of the application
     */
    @NotNull
    private String id;
    /**
     * The list of committee member usernames
     */
    @NotNull
    private List<String> members;
}
