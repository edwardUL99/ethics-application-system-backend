package ie.ul.ethics.scieng.applications.templates.repositories;

import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is used for saving an application template
 */
@Repository
public interface ApplicationTemplateRepository extends CrudRepository<ApplicationTemplate, Long> {
    /**
     * Find the application by its ID and not databaseId. I.e. the id defined in the JSON
     * @param id the id of the template, e.g. expedited, full
     * @return the list of saved templates with that ir
     */
    @Query("SELECT a FROM ApplicationTemplate a WHERE a.id = ?1")
    List<ApplicationTemplate> findByApplicationId(String id);
}
