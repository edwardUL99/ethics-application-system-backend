package ie.ul.ethics.scieng.applications.templates.components;

import ie.ul.ethics.scieng.applications.templates.SortingUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a container that wraps other components in a container.
 * Although this class has access to the title in the application component class, it is not intended to be a visible component
 */
@Getter
@Setter
@Entity
public class ContainerComponent extends CompositeComponent {
    /**
     * The ID of the container
     */
    private String id;
    /**
     * The list of components contained within the container
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
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
        super(ComponentType.CONTAINER, null);
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

    /**
     * Sort the list of child components
     */
    @Override
    public void sortComponents() {
        SortingUtils.sortComponents(components);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ContainerComponent that = (ContainerComponent) o;
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
