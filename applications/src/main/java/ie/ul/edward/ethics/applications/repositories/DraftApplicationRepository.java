package ie.ul.edward.ethics.applications.repositories;

import ie.ul.edward.ethics.applications.models.DraftApplication;
import ie.ul.edward.ethics.users.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository is used for saving application drafts
 */
@Repository
public interface DraftApplicationRepository extends CrudRepository<DraftApplication, Long> {
    /**
     * Find all the draft applications by the given user
     * @param user the user to find applications for
     * @return the list of draft applications
     */
    List<DraftApplication> findByUser(User user);
}
