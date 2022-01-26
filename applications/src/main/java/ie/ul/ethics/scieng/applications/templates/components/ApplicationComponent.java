package ie.ul.ethics.scieng.applications.templates.components;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

/**
 * This class represents an abstract application component. Any component that can be added to a ApplicationTemplate must extend
 * this class. It is merely a representational class with no inherent application functionality,
 * intended to be transmitted to the front-end where the functionality of form generation will take place
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ApplicationComponent {
    /**
     * The ID of the component in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    protected Long databaseId;
    /**
     * The type of the component
     */
    protected ComponentType type;
    /**
     * The component title
     */
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    protected String title;
    /**
     * This field indicates a component that can contain other components if true
     */
    @Setter(AccessLevel.NONE)
    private boolean composite;
    /**
     * The ID of the component that will be mapped to the ID of the html element
     */
    private String componentId;

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
    public ApplicationComponent(ComponentType type, String title, boolean composite) {
        this.type = type;
        this.title = title;
        this.composite = composite;
        this.componentId = UUID.randomUUID().toString();
    }
}
