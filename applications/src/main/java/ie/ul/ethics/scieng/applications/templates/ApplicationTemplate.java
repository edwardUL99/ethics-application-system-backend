package ie.ul.ethics.scieng.applications.templates;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a parsed application. It is merely a representational class with no inherent application functionality,
 * intended to be transmitted to the front-end where the functionality of form generation will take place.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ApplicationTemplate {
    /**
     * The database ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long databaseId;
    /**
     * The ID of the parsed application
     */
    private String id;
    /**
     * The name of the application
     */
    private String name;
    /**
     * The application's description
     */
    private String description;
    /**
     * The version of the application
     */
    private String version;
    /**
     * The application components
     */
    @ManyToMany(cascade = CascadeType.ALL)
    private List<ApplicationComponent> components = new ArrayList<>();

    /**
     * Sorts all the components
     */
    public void sort() {
        List<ApplicationComponent> toSort = new ArrayList<>();

        for (ApplicationComponent child : components) {
            SortingUtils.findSortRecursive(child, toSort);
            SortingUtils.sort(toSort);
        }

        Collections.sort(components);
    }

    /**
     * Determines if the template has a component with the given ID
     * @param componentId the ID to search for
     * @return true if it has an ID, false if not
     */
    public boolean hasComponent(String componentId) {
        for (ApplicationComponent component : components)
            if (component.matchesComponentId(componentId))
                return true;

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ApplicationTemplate that = (ApplicationTemplate) o;
        return Objects.equals(databaseId, that.databaseId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
