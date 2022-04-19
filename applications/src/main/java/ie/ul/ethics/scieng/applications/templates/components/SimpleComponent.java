package ie.ul.ethics.scieng.applications.templates.components;

import javax.persistence.Entity;

/**
 * This class represents a simple component that does not contain any other ApplicationComponents and is in of itself,
 * only an ApplicationComponent. A multi-part question is not considered composite as it has no getComponents method
 */
@Entity
public abstract class SimpleComponent extends ApplicationComponent {
    /**
     * Create a default simple component
     */
    public SimpleComponent() {
        this(null, null);
    }

    /**
     * Create a SimpleComponent
     * @param type the type of component
     * @param title the title of the component
     */
    public SimpleComponent(ComponentType type, String title) {
        super(type, title,false);
    }

    /**
     * Clear the database ID of this component and also any child components
     */
    @Override
    public void clearDatabaseIDs() {
        this.databaseId = null;
    }

    /**
     * If this component has a list of child components in some form or another this method, sorts them. Can be a noop
     */
    @Override
    public void sortComponents() {
        // no-op
    }
}
