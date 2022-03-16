package ie.ul.ethics.scieng.common.search.operators;

import ie.ul.ethics.scieng.common.search.SearchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class holds all registered operators
 */
public final class SearchOperators {
    /**
     * The registered operators
     */
    private static final Map<String, SearchOperator> operators = new HashMap<>();

    /**
     * List of search operator implementations
     */
    private static final List<SearchOperator> implementations = List.of(
      new LikeOperator(),
      new CaseInsensitiveLikeOperator(),
      new EqualsOperator(),
      new GreaterLessThanOperator(true),
      new GreaterLessThanOperator(false),
      new ContainsOperator()
    );

    static {
        implementations.forEach(SearchOperators::register);
    }

    /**
     * Prevent instantiation
     */
    private SearchOperators() {}

    /**
     * Register the operator with the search operators
     * @param operator the operator to register
     */
    public static void register(SearchOperator operator) {
        operators.put(operator.getOperator(), operator);
    }

    /**
     * Get the operator for the given operator literal
     * @param operator the operator literal
     * @return the search operator implementation
     * @throws SearchException if no operator exists
     */
    public static SearchOperator getOperator(String operator) {
        SearchOperator searchOperator = operators.get(operator);

        if (searchOperator == null)
            throw new SearchException("Operator " + operator + " has no implementation");

        return searchOperator;
    }
}
