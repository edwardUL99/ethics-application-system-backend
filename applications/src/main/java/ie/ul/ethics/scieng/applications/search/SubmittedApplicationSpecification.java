package ie.ul.ethics.scieng.applications.search;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.common.search.KeyMappings;
import ie.ul.ethics.scieng.common.search.SearchCriteria;
import ie.ul.ethics.scieng.common.search.operators.OperatorOverloads;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 * This class is used for searching draft applications
 */
public class SubmittedApplicationSpecification extends ApplicationSpecification<SubmittedApplication> {
    /**
     * Create an instance with the provided search criteria
     *
     * @param criteria the search criteria
     */
    public SubmittedApplicationSpecification(SearchCriteria criteria) {
        super(criteria);
    }

    /**
     * This method casts the root to a value that can be searched. By default, it does nothing and just returns
     * the root passed in. Can be overridden to return a different root
     *
     * @param root            the root to cast
     * @param criteriaBuilder the criteria builder building the Criteria
     * @return the cast root
     */
    @Override
    public Root<SubmittedApplication> castRoot(Root<SubmittedApplication> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.treat(root, SubmittedApplication.class);
    }

    /**
     * Register any key mappings to the provided KeyMappings object. Default implementation is to add no mappings.
     *
     * @param keyMappings the mappings to put the keys into
     */
    @Override
    public void registerKeyMappings(KeyMappings keyMappings) {
        super.registerKeyMappings(keyMappings);
        keyMappings.put("assigned", "assignedCommitteeMembers");
    }
}
