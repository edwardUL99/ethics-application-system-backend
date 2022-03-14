package ie.ul.ethics.scieng.common.search.operators;

import ie.ul.ethics.scieng.common.search.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * This operator represents an operator for the "=" which represents literal equals
 */
public class EqualsOperator extends BaseSearchOperator {
    /**
     * Construct the operator
     */
    public EqualsOperator() {
        super("=");
    }

    /**
     * Perform the operation on the root value
     *
     * @param rootValue       the value on the entity
     * @param criteriaBuilder the builder used to build the predicate
     * @param criteria        the search criteria
     * @return the built predicate
     */
    @Override
    public Predicate operate(Expression<?> rootValue, CriteriaBuilder criteriaBuilder, SearchCriteria criteria) {
        return criteriaBuilder.equal(rootValue, criteria.getValue());
    }
}
