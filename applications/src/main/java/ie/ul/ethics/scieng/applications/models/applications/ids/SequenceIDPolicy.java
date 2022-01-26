package ie.ul.ethics.scieng.applications.models.applications.ids;

import ie.ul.ethics.scieng.applications.repositories.SequenceIDRepository;

/**
 * This class provides an implementation that returns FREC numbers with the sequence ID
 */
public class SequenceIDPolicy implements ApplicationIDPolicy {
    /**
     * The repository used for saving sequence IDs
     */
    private final SequenceIDRepository repository;

    /**
     * Create a SequenceIDPolicy
     * @param repository for storing the sequence
     */
    public SequenceIDPolicy(SequenceIDRepository repository) {
        this.repository = repository;
    }

    /**
     * This method generates and returns the ID
     *
     * @return the ID for the application
     */
    @Override
    public String generate() {
        SequenceID sequenceID = new SequenceID();
        repository.save(sequenceID);

        return "REC-" + sequenceID.getId();
    }
}
