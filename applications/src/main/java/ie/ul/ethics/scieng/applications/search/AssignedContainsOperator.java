package ie.ul.ethics.scieng.applications.search;

import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.common.search.SearchCriteria;
import ie.ul.ethics.scieng.common.search.operators.ContainsOperator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

/**
 * An overloaded contains operator to check if a submitted application has the user assigned to it
 * Only makes sense on SubmittedApplications so should only be added to a SubmittedApplication specification
 */
public class AssignedContainsOperator extends ContainsOperator {
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
        CriteriaQuery<SubmittedApplication> query = criteriaBuilder.createQuery(SubmittedApplication.class);
        Root<SubmittedApplication> applicationRoot = query.from(SubmittedApplication.class);

        Subquery<String> subQuery = query.subquery(String.class);
        Root<SubmittedApplication> subApplication = subQuery.from(SubmittedApplication.class);
        Root<SubmittedApplication> correlated = subQuery.correlate(applicationRoot);

        Join<?, ?> assignedJoin = subApplication.joinList("assignedCommitteeMembers");
        Join<?, ?> assignedUserJoin = assignedJoin.join("user");
        subQuery.select(assignedUserJoin.get("username"));
        subQuery.where(criteriaBuilder.equal(assignedJoin.get("applicationId"), correlated.get("applicationId")));

        query.select(applicationRoot)
                .where(criteriaBuilder.in(criteriaBuilder.literal(criteria.getValue())).value(subQuery));

        return query.getRestriction();
    }
}
