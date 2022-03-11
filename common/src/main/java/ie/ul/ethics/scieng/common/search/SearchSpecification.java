package ie.ul.ethics.scieng.common.search;

import ie.ul.ethics.scieng.common.search.operators.OperatorOverloads;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Set;

/**
 * This class is a base class that any Specification class can extend and uses a search criteria.
 *
 * A SearchSpecification is a class that defines how a SearchCriteria should be converted to a JPA Predicate. It allows
 * the definition of mapping user-friendly search fields to the db property names, functions to transform/convert search values
 * into forms suitable for searching, and overloaded search operators to specifically handle that criteria
 *
 * @param <T> the type of the object that is being searched
 */
public abstract class SearchSpecification<T> implements Specification<T> {
    /**
     * The criteria object to search with
     */
    protected final SearchCriteria criteria;
    /**
     * The key mappings to map query keys
     */
    private final KeyMappings keyMappings = new KeyMappings();
    /**
     * The mapping of value converters
     */
    private final ValueConverters converters = new ValueConverters();
    /**
     * The mapping of operator overloads
     */
    private final OperatorOverloads overloads = new OperatorOverloads();

    /**
     * Create an instance with the provided search criteria
     * @param criteria the search criteria
     */
    public SearchSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
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
            String splitKey = keyMappings.getMappedKey(split[0]);

            Path<?> rootValue = root.get(splitKey);

            for (int i = 1; i < split.length; i++) {
                splitKey = keyMappings.getMappedKey(split[i]);
                rootValue = rootValue.get(splitKey);
            }

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

        registerKeyMappings(keyMappings);
        criteriaKey = keyMappings.getMappedKey(criteriaKey);

        registerValueConverters(converters);
        criteria.setValue(converters.getConverter(criteriaKey).apply(criteria.getValue()));
        registerOperatorOverloads(overloads);

        if (supportedOperations().contains(operation)) {
            try {
                Expression<?> rootValue = this.parseValue(root, criteriaKey);
                return overloads.getOperator(criteriaKey, operation).operate(rootValue, criteriaBuilder, criteria);
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
     * Register any key mappings to the provided KeyMappings object. Default implementation is to add no mappings.
     * @param keyMappings the mappings to put the keys into
     */
    public void registerKeyMappings(KeyMappings keyMappings) {}

    /**
     * Register any value converters to the provided ValueConverters object. These converters will be used in the creation of
     * the search predicate. Default implementation is to add no converters but can be overridden
     * @param valueConverters the converters to register to
     */
    public void registerValueConverters(ValueConverters valueConverters) {}

    /**
     * Register any overloaded search operators for fields of the object. These operators overload the default registered operators for that
     * criteria key
     * @param overloads the overloaded operators to register to
     */
    public void registerOperatorOverloads(OperatorOverloads overloads) {}

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
