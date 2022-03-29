package ie.ul.ethics.scieng.applications.templates.components;

import ie.ul.ethics.scieng.applications.templates.SortingUtils;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a section component that can contain other components within the section
 */
@Getter
@Setter
@Entity
public class SectionComponent extends CompositeComponent {
    /**
     * The section's description
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String description;
    /**
     * The sub-components of this section
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<ApplicationComponent> components;
    /**
     * This field indicates that when the whole section's questions are answered, the application being filled out should be auto-saved.
     * Most suitable for a draft application
     */
    private boolean autoSave;

    /**
     * Create a default SectionComponent
     */
    public SectionComponent() {
        this(null, null, new ArrayList<>(), true);
    }

    /**
     * Create a SectionComponent
     * @param title the title of the section
     * @param description the section description
     * @param components the components in the section
     * @param autoSave true to auto-save when the section is filled
     */
    public SectionComponent(String title, String description, List<ApplicationComponent> components, boolean autoSave) {
        super(ComponentType.SECTION, title);
        this.description = description;
        this.components = components;
        this.autoSave = autoSave;
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
        SectionComponent that = (SectionComponent) o;
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
