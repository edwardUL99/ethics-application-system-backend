package ie.ul.ethics.scieng.applications.models.applications.ids;

/**
 * This interface represents a policy for generating application IDs
 */
public interface ApplicationIDPolicy {
    /**
     * This method generates and returns the ID
     * @return the ID for the application
     */
    String generate();
}
