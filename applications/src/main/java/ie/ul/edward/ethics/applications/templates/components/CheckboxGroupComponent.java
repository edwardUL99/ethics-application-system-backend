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
public class CheckboxGroupComponent extends ApplicationComponent {
    /**
     * The default branch to execute
     */
    private Branch defaultBranch;
    /**
     * The list of checkboxes in the group
     */
    private List<Checkbox> checkboxes;

    /**
     * Create a default CheckboxGroupComponent
     */
    public CheckboxGroupComponent() {
        this(null, null, new ArrayList<>());
    }

    /**
     * Create a CheckboxGroupComponent
     * @param title the title of the component
     * @param defaultBranch the default branch to execute
     * @param checkboxes the list of checkboxes in the group
     */
    public CheckboxGroupComponent(String title, Branch defaultBranch, List<Checkbox> checkboxes) {
        super(ComponentTypes.CHECKBOX_GROUP, title);
        this.defaultBranch  = defaultBranch;
        this.checkboxes = checkboxes;
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
