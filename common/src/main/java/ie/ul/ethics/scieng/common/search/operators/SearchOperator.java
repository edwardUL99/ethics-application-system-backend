package ie.ul.ethics.scieng.common.search.operators;

import ie.ul.ethics.scieng.common.search.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

/**
 * This interface represents a search operator
 */
public interface SearchOperator {
    /**
     * Get the operator literal as seen in the search string
     * @return the operator literal
     */
    String getOperator();

    /**
     * Perform the operation on the root value
     * @param rootValue the value on the entity
     * @param criteriaBuilder the builder used to build the predicate
     * @param criteria the search criteria
     * @return the built predicate
     */
    Predicate operate(Expression<?> rootValue, CriteriaBuilder criteriaBuilder, SearchCriteria criteria);
}
