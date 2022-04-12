package ie.ul.ethics.scieng.applications.templates.components;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This class represents a component that represents a group of checkboxes which each execute a default
 * branch if checked, unless they define their own branch
 */
@Getter
@Setter
@Entity
public class CheckboxGroupComponent extends QuestionComponent {
    /**
     * The default branch to execute
     */
    @OneToOne(cascade = CascadeType.ALL)
    private Branch defaultBranch;
    /**
     * The list of checkboxes in the group
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Checkbox> checkboxes;
    /**
     * Determines if multiple options can be chosen in the group, the default is false
     */
    private boolean multiple;
    /**
     * A variable to determine if the component is required
     */
    private boolean required;

    /**
     * Create a default CheckboxGroupComponent
     */
    public CheckboxGroupComponent() {
        this(null, null, new ArrayList<>(), false, false);
    }

    /**
     * Create a CheckboxGroupComponent
     * @param title the title of the component
     * @param defaultBranch the default branch to execute
     * @param checkboxes the list of checkboxes in the group
     * @param multiple true if multiple can be chosen, false if not
     * @param required determines if the component is required or not
     */
    public CheckboxGroupComponent(String title, Branch defaultBranch, List<Checkbox> checkboxes, boolean multiple, boolean required) {
        super(ComponentType.CHECKBOX_GROUP, title, null, null, required);
        this.defaultBranch  = defaultBranch;
        this.checkboxes = checkboxes;
        this.multiple = multiple;
    }

    /**
     * Clear the database ID of this component and also any child components
     */
    @Override
    public void clearDatabaseIDs() {
        this.databaseId = null;

        if (this.defaultBranch != null)
            this.defaultBranch.setBranchId(null);

        this.checkboxes.forEach(checkbox -> {
            checkbox.setId(null);

            if (checkbox.branch != null)
                checkbox.branch.setBranchId(null);
        });
    }

    /**
     * This class represents a checkbox in the group
     */
    @Getter
    @Setter
    @Entity
    @NoArgsConstructor
    public static class Checkbox {
        /**
         * The database ID
         */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        /**
         * The title of the checkbox
         */
        private String title;
        /**
         * The branch to override default branch with
         */
        @OneToOne(cascade = CascadeType.ALL)
        private Branch branch;
        /**
         * A unique UUID to uniquely identify this checkbox group
         */
        private String identifier = UUID.randomUUID().toString();

        /**
         * Create a Checkbox
         * @param id the database ID
         * @param title the title of the checkbox
         * @param branch the checkbox branch to override default branch
         */
        public Checkbox(Long id, String title, Branch branch) {
            this.id = id;
            this.title = title;
            this.branch = branch;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Checkbox checkbox = (Checkbox) o;
            return Objects.equals(id, checkbox.id) && Objects.equals(title, checkbox.title) && Objects.equals(branch, checkbox.branch);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return Objects.hash(id, title, branch);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CheckboxGroupComponent that = (CheckboxGroupComponent) o;
        return databaseId != null && Objects.equals(databaseId, that.databaseId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
