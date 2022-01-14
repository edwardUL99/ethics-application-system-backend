package ie.ul.edward.ethics.applications.parsing.components;

/**
 * This class represents a branching action for an application that is triggered when a condition turns true
 */
public abstract class Branch {
    /**
     * The type of branch
     */
    protected String type;

    /**
     * Create a default branch
     */
    public Branch() {
        this(null);
    }

    /**
     * Create a branch
     * @param type the type of branch
     */
    public Branch(String type) {
        this.type = type;
    }
}
