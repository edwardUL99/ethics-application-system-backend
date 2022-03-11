package ie.ul.ethics.scieng.applications.search;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.common.search.KeyMappings;
import ie.ul.ethics.scieng.common.search.SearchCriteria;
import ie.ul.ethics.scieng.common.search.SearchSpecification;
import ie.ul.ethics.scieng.common.search.ValueConverters;

import java.util.Set;

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
     * Register any key mappings to the provided KeyMappings object. Default implementation is to add no mappings.
     *
     * @param keyMappings the mappings to put the keys into
     */
    @Override
    public void registerKeyMappings(KeyMappings keyMappings) {
        super.registerKeyMappings(keyMappings);

        keyMappings.put("id", "applicationId");
        keyMappings.put("dbId", "id");
    }

    /**
     * Register any value converters to the provided ValueConverters object. These converters will be used in the creation of
     * the search predicate. Default implementation is to add no converters but can be overridden
     *
     * @param valueConverters the converters to register to
     */
    @Override
    public void registerValueConverters(ValueConverters valueConverters) {
        super.registerValueConverters(valueConverters);

        valueConverters.addConverter("status", value -> {
            if (value instanceof String) {
                return ApplicationStatus.valueOf((String)value);
            } else {
                return value;
            }
        });
    }
}
