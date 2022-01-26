package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.users.models.User;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * This class represents an accept resubmitted request that has been mapped
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class MappedAcceptResubmittedRequest {
    /**
     * The application being accepted
     */
    private Application application;
    /**
     * The list of committee members to assign
     */
    private List<User> committeeMembers;
}
