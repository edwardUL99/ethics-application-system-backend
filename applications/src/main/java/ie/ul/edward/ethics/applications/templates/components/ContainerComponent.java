package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a container that wraps other components in a container.
 * Although this class has access to the title in the application component class, it is not intended to be a visible component
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class ContainerComponent extends CompositeComponent {
    /**
     * The ID of the container
     */
    private String id;
    /**
     * The list of components contained within the container
     */
    private List<ApplicationComponent> components;

    /**
     * Create a default container component
     */
    public ContainerComponent() {
        this(null, new ArrayList<>());
    }

    /**
     * Create a container component
     * @param id the id of the container
     * @param components the list of components within the container
     */
    public ContainerComponent(String id, List<ApplicationComponent> components) {
        super(ComponentTypes.CONTAINER, null);
        this.id = id;
        this.components = components;
    }

    /**
     * Overridden to throw an IllegalStateException if the title is attempted to be set
     */
    @Override
    public void setTitle(String title) {
        throw new IllegalStateException("You cannot set a title on a ContainerComponent");
    }
}
