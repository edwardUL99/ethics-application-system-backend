package ie.ul.ethics.scieng.common.search.operators;

import ie.ul.ethics.scieng.common.search.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * This operator represents a like operator that operates case insensitively
 */
public class CaseInsensitiveLikeOperator extends BaseSearchOperator {
    /**
     * Construct the operator
     */
    public CaseInsensitiveLikeOperator() {
        super(":~");
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
            Expression<String> stringValue = criteriaBuilder.lower((Expression<String>) rootValue);
            return criteriaBuilder.like(stringValue, "%" + ((String)criteria.getValue()).toLowerCase() + "%");
        } else {
            return criteriaBuilder.equal(rootValue, criteria.getValue());
        }
    }
}
