package ie.ul.ethics.scieng.common.search.operators;

import ie.ul.ethics.scieng.common.search.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * This operator performs a "LIKE" operation, represented with the ":" symbol
 */
public class LikeOperator extends BaseSearchOperator {
    /**
     * Construct the operator
     */
    public LikeOperator() {
        super(":");
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
    @SuppressWarnings("unchecked")
    public Predicate operate(Expression<?> rootValue, CriteriaBuilder criteriaBuilder, SearchCriteria criteria) {
        if (rootValue.getJavaType() == String.class) {
            return criteriaBuilder.like((Expression<String>) rootValue, "%" + criteria.getValue() + "%");
        } else {
            return criteriaBuilder.equal(rootValue, criteria.getValue());
        }
    }
}
