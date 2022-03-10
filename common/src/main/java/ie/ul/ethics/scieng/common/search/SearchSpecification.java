package ie.ul.ethics.scieng.common.search;

import ie.ul.ethics.scieng.common.search.operators.SearchOperators;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * This class is a base class that any Specification class can extend and uses a search criteria
 * @param <T> the type of the object that is being searched
 */
public abstract class SearchSpecification<T> implements Specification<T> {
    /**
     * The criteria object to search with
     */
    protected final SearchCriteria criteria;
    /**
     * A lazily initialised map of key mappings
     */
    protected Map<String, String> mappings;
    /**
     * A lazily initialised map of converters
     */
    protected Map<String, Function<Object, Object>> converters;

    /**
     * Create an instance with the provided search criteria
     * @param criteria the search criteria
     */
    public SearchSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    /**
     * Lazily initialise the key mappings from the overridable keyMapppings method
     * @return the initialised key mappings
     */
    private Map<String, String> getKeyMappings() {
        if (this.mappings == null) {
            this.mappings = keyMappings();
        }

        return this.mappings;
    }

    /**
     * Lazily initialise the converters from the overridable valueConverters method
     * @return the map of converters
     */
    private Map<String, Function<Object, Object>> getConverters() {
        if (this.converters == null) {
            this.converters = this.valueConverters();
        }

        return this.converters;
    }

    /**
     * Parse the value. If the key is nested with parent.child, the property path will be followed
     * @param root the root representing the entity
     * @param key the key of the property
     * @return the parsed expression
     */
    private Expression<?> parseValue(Root<?> root, String key) {
        if (key.contains(".")) {
            String[] split = key.split("\\.");

            Path<?> rootValue = root.get(split[0]);

            for (int i = 1; i < split.length; i++)
                rootValue = rootValue.get(split[i]);

            return rootValue;
        } else {
            return root.get(key);
        }
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        root = castRoot(root, criteriaBuilder);

        String operation = criteria.getOperation();
        String criteriaKey = criteria.getKey();

        Map<String, String> mappings = getKeyMappings();

        if (mappings != null)
            criteriaKey = mappings.getOrDefault(criteriaKey, criteriaKey);

        Map<String, Function<Object, Object>> converters = this.getConverters();

        if (converters != null) {
            Function<Object, Object> converter = converters.getOrDefault(criteriaKey, value -> value);
            criteria.setValue(converter.apply(criteria.getValue()));
        }

        if (supportedOperations().contains(operation)) {
            try {
                Expression<?> rootValue = this.parseValue(root, criteriaKey);
                return SearchOperators.getOperator(operation).operate(rootValue, criteriaBuilder, criteria);
            } catch (Exception ex) {
                throw new SearchException("An error occurred during search", ex);
            }
        }

        return null;
    }

    /**
     * Returns a set of supported operations.
     * @return the set of supported operations by this specification
     */
    public abstract Set<String> supportedOperations();

    /**
     * This is a method that can be overridden to return a map that maps a criteria key to a field name. This can be useful
     * if the query key is separate to the name of the actual property on the entity, for example id can map to applicationId,
     * while dbId maps to id
     * @return null if not overridden or the mapping
     */
    public Map<String, String> keyMappings() {
        return null;
    }

    /**
     * A map of key names to a function that takes the value and returns a mapped value. Default is to return null
     * @return the map of value converters
     */
    public Map<String, Function<Object, Object>> valueConverters() {
        return null;
    }

    /**
     * This method casts the root to a value that can be searched. By default, it does nothing and just returns
     * the root passed in. Can be overridden to return a different root
     * @param root the root to cast
     * @param criteriaBuilder the criteria builder building the Criteria
     * @return the cast root
     */
    public Root<T> castRoot(Root<T> root, CriteriaBuilder criteriaBuilder) {
        return root;
    }
}
