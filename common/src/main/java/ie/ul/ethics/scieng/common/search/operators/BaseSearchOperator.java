package ie.ul.ethics.scieng.common.search.operators;

/**
 * This class provides a base search operator
 */
public abstract class BaseSearchOperator implements SearchOperator {
    /**
     * The operator literal
     */
    protected final String operator;

    /**
     * Construct the operator
     * @param operator the operator literal
     */
    protected BaseSearchOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Get the operator literal as seen in the search string
     *
     * @return the operator literal
     */
    @Override
    public String getOperator() {
        return operator;
    }
}
