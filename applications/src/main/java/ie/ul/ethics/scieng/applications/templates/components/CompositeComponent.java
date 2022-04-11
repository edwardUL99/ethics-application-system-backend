package ie.ul.ethics.scieng.applications.templates.components;

import javax.persistence.Entity;
import java.util.List;

/**
 * This class is the base class for any application component that recursively contains other ApplicationComponent,
 * e.g. a container or section
 */
@Entity
public abstract class CompositeComponent extends ApplicationComponent {
    /**
     * Create a default composite component
     */
    public CompositeComponent() {
        this(null, null);
    }

    /**
     * Create a CompositeComponent
     * @param type the type of component
     * @param title the title of the component
     */
    public CompositeComponent(ComponentType type, String title) {
        super(type, title, true);
    }

    /**
     * Get the components that are contained within this composite component
     * @return the list of sub-components
     */
    public abstract List<ApplicationComponent> getComponents();

    /**
     * Sort the list of child components
     */
    public abstract void sortComponents();

    /**
     * Clear the database ID of this component and also any child components
     */
    @Override
    public void clearDatabaseIDs() {
        this.databaseId = null;

        for (ApplicationComponent component : getComponents())
            component.clearDatabaseIDs();
    }
}
