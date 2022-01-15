package ie.ul.edward.ethics.applications.templates.components;

import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents an abstract application component. Any component that can be added to a ApplicationTemplate must extend
 * this class. It is merely a representational class with no inherent application functionality,
 * intended to be transmitted to the front-end where the functionality of form generation will take place
 */
@Getter
@Setter
public abstract class ApplicationComponent {
    /**
     * The type of the component
     */
    @Setter(AccessLevel.NONE)
    protected String type;
    /**
     * The component title
     */
    protected String title;
    /**
     * This field indicates a component that can contain other components if true
     */
    @Setter(AccessLevel.NONE)
    private boolean composite;

    /**
     * Create a default ApplicationComponent
     */
    public ApplicationComponent() {
        this(null, null, false);
    }

    /**
     * Create an ApplicationComponent with the provided type, title and description
     * @param type the type of the component
     * @param title the component title
     * @param composite true if a composite component (i.e. has a getComponents() method), false if not
     */
    public ApplicationComponent(String type, String title, boolean composite) {
        this.setType(type);
        this.title = title;
        this.composite = composite;
    }

    /**
     * Set the type of the component
     * @param type the component type. Must be registered in {@link ComponentTypes}
     */
    public void setType(String type) {
        if (type != null && !ComponentTypes.isValidComponentType(type))
            throw new ApplicationParseException("The type " + type + " is not a registered type in class " + ComponentTypes.class.getName());

        this.type = type;
    }
}
