package ie.ul.ethics.scieng.applications.repositories;

import ie.ul.ethics.scieng.applications.models.applications.ids.SequenceID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository stores sequence IDs
 */
@Repository
public interface SequenceIDRepository extends CrudRepository<SequenceID, Long> {
}
