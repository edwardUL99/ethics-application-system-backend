package ie.ul.edward.ethics.applications.templates.components;

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
}
