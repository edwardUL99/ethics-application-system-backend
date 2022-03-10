package ie.ul.ethics.scieng.applications.search;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.common.search.SearchCriteria;
import ie.ul.ethics.scieng.common.search.SearchSpecification;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This base class represents a specification for searching applications
 */
public class ApplicationSpecification<T extends Application> extends SearchSpecification<T> {
    /**
     * The regex outlining the operations this ApplicationSpecification provides
     */
    public static final String OPERATION_PATTERN = "([A-Za-z0-9_.]+?)(:=|:~|:|=|<|>)(.+?),";

    /**
     * Create an instance with the provided search criteria
     *
     * @param criteria the search criteria
     */
    public ApplicationSpecification(SearchCriteria criteria) {
        super(criteria);
    }

    /**
     * Returns a set of supported operations.
     *
     * @return the set of supported operations by this specification
     */
    @Override
    public Set<String> supportedOperations() {
        return Set.of(":", ":~", "=", ":=", "<", ">");
    }

    /**
     * This is a method that can be overridden to return a map that maps a criteria key to a field name. This can be useful
     * if the query key is separate to the name of the actual property on the entity, for example id can map to applicationId,
     * while dbId maps to id
     *
     * @return null if not overridden or the mapping
     */
    @Override
    public Map<String, String> keyMappings() {
        Map<String, String> mappings = new HashMap<>();

        mappings.put("id", "applicationId");
        mappings.put("dbId", "id");

        return mappings;
    }

    /**
     * A map of key names to a function that takes the value and returns a mapped value. Default is to return null
     *
     * @return the map of value converters
     */
    @Override
    public Map<String, Function<Object, Object>> valueConverters() {
        Map<String, Function<Object, Object>> converters = new HashMap<>();

        converters.put("status", value -> {
            if (value instanceof String) {
                return ApplicationStatus.valueOf((String)value);
            } else {
                return value;
            }
        });

        return converters;
    }
}
