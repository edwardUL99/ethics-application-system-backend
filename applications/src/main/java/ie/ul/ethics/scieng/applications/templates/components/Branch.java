package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * This class represents a branching action for an application that is triggered when a condition turns true
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
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
    protected ComponentType type;

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
    public Branch(ComponentType type) {
        this.type = type;
    }
}
