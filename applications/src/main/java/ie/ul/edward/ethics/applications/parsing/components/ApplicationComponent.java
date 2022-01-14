package ie.ul.edward.ethics.applications.parsing.components;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents an abstract application component. Any component that can be added to a ParsedApplication must extend
 * this class. It is merely a representational class with no inherent application functionality,
 * intended to be transmitted to the front-end where the functionality of form generation will take place
 */
@Getter
@Setter
public abstract class ApplicationComponent {
    /**
     * The type of the component
     */
    protected String type;
    /**
     * The component title
     */
    protected String title;

    /**
     * Create a default ApplicationComponent
     */
    public ApplicationComponent() {
        this(null, null);
    }

    /**
     * Create an ApplicationComponent with the provided type, title and description
     * @param type the type of the component
     * @param title the component title
     */
    public ApplicationComponent(String type, String title) {
        this.type = type;
        this.title = title;
    }
}
