package ie.ul.edward.ethics.applications.templates;

import ie.ul.edward.ethics.applications.templates.components.ApplicationComponent;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a parsed application. It is merely a representational class with no inherent application functionality,
 * intended to be transmitted to the front-end where the functionality of form generation will take place
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationTemplate {
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
    private List<ApplicationComponent> components = new ArrayList<>();
}
