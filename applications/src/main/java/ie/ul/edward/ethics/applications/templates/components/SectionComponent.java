package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a section component that can contain other components within the section
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class SectionComponent extends ApplicationComponent {
    /**
     * The section's description
     */
    private String description;
    /**
     * The sub-components of this section
     */
    private List<ApplicationComponent> components;

    /**
     * Create a default SectionComponent
     */
    public SectionComponent() {
        this(null, null, new ArrayList<>());
    }

    /**
     * Create a SectionComponent
     * @param title the title of the section
     * @param description the section description
     * @param components the components in the section
     */
    public SectionComponent(String title, String description, List<ApplicationComponent> components) {
        super(ComponentTypes.SECTION, title);
        this.description = description;
        this.components = components;
    }
}
