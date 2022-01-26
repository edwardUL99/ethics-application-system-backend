package ie.ul.ethics.scieng.applications.templates.repositories;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository provides a repository for ApplicationComponents
 */
@Repository
public interface ApplicationComponentRepository extends CrudRepository<ApplicationComponent, Long> {
}
