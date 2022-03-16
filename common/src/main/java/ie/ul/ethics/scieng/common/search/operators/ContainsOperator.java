package ie.ul.ethics.scieng.common.search.operators;

import ie.ul.ethics.scieng.common.search.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * This class represents an operator that can check if a string contains a substring. If the field being checked is a string,
 * it is equivalent to the case-sensitive like (:) operator
 */
public class ContainsOperator extends BaseSearchOperator {
    /**
     * Construct the operator
     */
    protected ContainsOperator() {
        super(":=");
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
        Class<?> type = rootValue.getJavaType();

        if (type == String.class) {
            return SearchOperators.getOperator(":").operate(rootValue, criteriaBuilder, criteria);
        } else if (type == List.class) {
            return criteriaBuilder.<Object>in(rootValue).value(criteria.getValue());
        } else {
            return null;
        }
    }
}
