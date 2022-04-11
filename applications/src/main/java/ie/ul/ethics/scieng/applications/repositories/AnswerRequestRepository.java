package ie.ul.ethics.scieng.applications.repositories;

import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AnswerRequest;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * This interface represents a repository for storing answer requests
 */
public interface AnswerRequestRepository extends CrudRepository<AnswerRequest, Long> {
    /**
     * Find all the requests by the given username
     * @param username the username of the user to find the requests with
     * @return the found requests
     */
    List<AnswerRequest> findByUser_username(String username);

    /**
     * Delete all requests with the given application ID
     * @param id the requests to delete with an application with the given ID
     */
    void deleteByApplication_id(Long id);
}
