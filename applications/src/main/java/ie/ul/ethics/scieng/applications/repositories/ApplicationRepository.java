package ie.ul.ethics.scieng.applications.repositories;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.common.search.SearchableRepository;
import ie.ul.ethics.scieng.users.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This repository is used for saving application drafts
 */
@Repository
public interface ApplicationRepository extends CrudRepository<Application, Long>, SearchableRepository<Application> {
    /**
     * Find the application by its ApplicationId attribute
     * @param applicationId the ethics ID to find the application by
     * @return the found application, or an empty optional if not
     */
    Optional<Application> findByApplicationId(String applicationId);

    /**
     * Find all the draft applications by the given user
     * @param user the user to find applications for
     * @return the list of found applications
     */
    List<Application> findByUser(User user);

    /**
     * Find all applications by status
     * @param status the status of the applications
     * @return the list of found applications
     */
    List<Application> findByStatus(ApplicationStatus status);
}
