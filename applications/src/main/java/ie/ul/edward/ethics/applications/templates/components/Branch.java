package ie.ul.edward.ethics.applications.templates.components;

import javax.persistence.*;

/**
 * This class represents a branching action for an application that is triggered when a condition turns true
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Branch {
    /**
     * The branch ID of the branch
     */
    @Id
    @GeneratedValue
    protected Long branchId;
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
