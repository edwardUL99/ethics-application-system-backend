package ie.ul.ethics.scieng.applications.models.mapping;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * This class represents a request to accept an application that has been resubmitted
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class AcceptResubmittedRequest {
    /**
     * The id of the application (not the dbId)
     */
    private String id;
    /**
     * The list of committee member usernames to add to the application
     */
    private List<String> committeeMembers;
}
