package ie.ul.ethics.scieng.users.search;

import ie.ul.ethics.scieng.common.search.SearchCriteria;
import ie.ul.ethics.scieng.common.search.SearchSpecification;
import ie.ul.ethics.scieng.users.models.User;

import java.util.Set;

/**
 * This class represents a specification for searching for users
 */
public class UserSpecification extends SearchSpecification<User> {
    /**
     * The regex outlining the operations this UserSpecification provides
     */
    public static final String OPERATION_PATTERN = "([A-Za-z0-9_.]+?)(:~|:|=)(.+?),";

    /**
     * Construct a UserSpecification with the provided criteria
     * @param criteria the criteria to use for search
     */
    public UserSpecification(SearchCriteria criteria) {
        super(criteria);
    }

    /**
     * Returns a set of supported operations.
     *
     * @return the set of supported operations by this specification
     */
    @Override
    public Set<String> supportedOperations() {
        return Set.of(":", ":~", "=");
    }
}
