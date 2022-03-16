package ie.ul.ethics.scieng.common.search.operators;

import java.util.HashMap;
import java.util.Map;

/**
 * This class allows the specification of an operator that can be overloaded based on a criteria key
 */
public class OperatorOverloads {
    /**
     * The mapping of criteria key to the map of operator literals and search operators
     */
    private final Map<String, Map<String, SearchOperator>> overloads = new HashMap<>();

    /**
     * Register the operator to overload when the criteria key is hit
     * @param criteriaKey the key in the criteria
     * @param operator the operator to overload
     */
    public void addOperatorOverload(String criteriaKey, SearchOperator operator) {
        Map<String, SearchOperator> map = this.overloads.computeIfAbsent(criteriaKey, k -> new HashMap<>());
        map.put(operator.getOperator(), operator);
    }

    /**
     * Gets the defined overload operator for the criteria key if it exists. If it doesn't the default registered operator
     * for the operator literal is returned
     * @param criteriaKey the key to find the overloaded operator for
     * @param operator the operator literal
     * @return the found operator if exists or the default registered operator
     */
    public SearchOperator getOperator(String criteriaKey, String operator) {
        Map<String, SearchOperator> map = this.overloads.get(criteriaKey);

        if (map != null && map.containsKey(operator))
            return map.get(operator);
        else
            return SearchOperators.getOperator(operator);
    }
}
