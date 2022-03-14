package ie.ul.ethics.scieng.common.search;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * This is a common "tag" interface that can be extended by any repository implementing search capability
 * @param <T> the type that is being searched
 */
public interface SearchableRepository<T> extends JpaSpecificationExecutor<T> {
}
