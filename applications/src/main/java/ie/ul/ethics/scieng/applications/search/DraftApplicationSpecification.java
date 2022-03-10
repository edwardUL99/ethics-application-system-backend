package ie.ul.ethics.scieng.applications.search;

import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.common.search.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 * This class is used for searching draft applications
 */
public class DraftApplicationSpecification extends ApplicationSpecification<DraftApplication> {
    /**
     * Create an instance with the provided search criteria
     *
     * @param criteria the search criteria
     */
    public DraftApplicationSpecification(SearchCriteria criteria) {
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
    public Root<DraftApplication> castRoot(Root<DraftApplication> root, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.treat(root, DraftApplication.class);
    }
}
