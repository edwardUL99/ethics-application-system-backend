package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import java.util.Objects;

/**
 * This branch executes a specified action when triggered
 */
@Getter
@Setter
@Entity
public class ActionBranch extends Branch {
    /**
     * The name of the action to execute
     */
    private String action;
    /**
     * This comment can be displayed when the action is triggered
     */
    private String comment;

    /**
     * Create a default ActionBranch
     */
    public ActionBranch() {
        this(null, null);
    }

    /**
     * Create an ActionBranch
     * @param action the action to execute
     * @param comment the comment to display when the action is triggered
     */
    public ActionBranch(String action, String comment) {
        super(ComponentType.ACTION_BRANCH);
        this.action = action;
        this.comment = comment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ActionBranch that = (ActionBranch) o;
        return branchId != null && Objects.equals(branchId, that.branchId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
