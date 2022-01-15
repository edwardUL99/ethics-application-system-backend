package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * This branch executes a specified action when triggered
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
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

    }

    /**
     * Create an ActionBranch
     * @param action the action to execute
     * @param comment the comment to display when the action is triggered
     */
    public ActionBranch(String action, String comment) {
        super(ComponentTypes.ACTION_BRANCH);
        this.action = action;
        this.comment = comment;
    }
}
