package ie.ul.edward.ethics.applications.templates.components;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a component that represents a group of checkboxes which each execute a default
 * branch if checked, unless they define their own branch
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class CheckboxGroupComponent extends SimpleComponent {
    /**
     * The default branch to execute
     */
    private Branch defaultBranch;
    /**
     * The list of checkboxes in the group
     */
    private List<Checkbox> checkboxes;
    /**
     * Determines if multiple options can be chosen in the group, the default is false
     */
    private boolean multiple;

    /**
     * Create a default CheckboxGroupComponent
     */
    public CheckboxGroupComponent() {
        this(null, null, new ArrayList<>(), false);
    }

    /**
     * Create a CheckboxGroupComponent
     * @param title the title of the component
     * @param defaultBranch the default branch to execute
     * @param checkboxes the list of checkboxes in the group
     * @param multiple true if multiple can be chosen, false if not
     */
    public CheckboxGroupComponent(String title, Branch defaultBranch, List<Checkbox> checkboxes, boolean multiple) {
        super(ComponentTypes.CHECKBOX_GROUP, title);
        this.defaultBranch  = defaultBranch;
        this.checkboxes = checkboxes;
        this.multiple = multiple;
    }

    /**
     * This class represents a checkbox in the group
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Checkbox {
        /**
         * The title of the checkbox
         */
        private String title;
        /**
         * The branch to override default branch with
         */
        private Branch branch;
    }
}
