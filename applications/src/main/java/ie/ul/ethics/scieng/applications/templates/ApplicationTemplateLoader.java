package ie.ul.ethics.scieng.applications.templates;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * This class provides a loader of the application templates from configuration. Whenever you want to send templates in a request,
 * Autowire the loader and call loadTemplates()
 */
public class ApplicationTemplateLoader {
    /**
     * The list of resources to parse
     */
    private final List<Resource> resources;
    /**
     * The application parser to use
     */
    private final ApplicationParser applicationParser;

    /**
     * Create an ApplicationTemplateLoader
     * @param resources the list of resources to parse
     * @param applicationParser parser to use
     */
    public ApplicationTemplateLoader(List<Resource> resources, ApplicationParser applicationParser) {
        this.resources = resources;
        this.applicationParser = applicationParser;
    }

    /**
     * Load the application templates
     * @return the array of templates
     */
    public ApplicationTemplate[] loadTemplates() {
        try {
            InputStream[] inputStreams = new InputStream[resources.size()];

            for (int i = 0; i < resources.size(); i++) {
                inputStreams[i] = resources.get(i).getInputStream();
            }

            return applicationParser.parse(inputStreams);
        } catch (IOException ex) {
            throw new ApplicationParseException("Failed to parse application JSON", ex);
        }
    }
}
