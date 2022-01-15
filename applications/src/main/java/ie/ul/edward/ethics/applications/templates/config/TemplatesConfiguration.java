package ie.ul.edward.ethics.applications.templates.config;

import ie.ul.edward.ethics.applications.templates.ApplicationParser;
import ie.ul.edward.ethics.applications.templates.ApplicationTemplate;
import ie.ul.edward.ethics.applications.templates.converters.Converters;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The configuration class for application parsing
 */
@Configuration
@Order(1)
@Log4j2
public class TemplatesConfiguration {
    /**
     * The configuration properties
     */
    private final TemplatesConfigurationProperties properties;
    /**
     * The parser for parsing applications
     */
    private final ApplicationParser applicationParser;

    /**
     * Create a TemplatesConfiguration object
     * @param properties the properties for templates
     * @param applicationParser the parser for parsing applications
     */
    @Autowired
    public TemplatesConfiguration(TemplatesConfigurationProperties properties, ApplicationParser applicationParser) {
        this.properties = properties;
        this.applicationParser = applicationParser;

        log.info("Using ApplicationParser {} to parse application templates", applicationParser);
    }

    /**
     * Get the resource file for the application's file
     * @return the appropriate resource
     */
    private Resource[] getApplicationResources() {
        List<Resource> resources = new ArrayList<>();

        for (String applicationPath : properties.getFilePaths()) {
            String classpath = "classpath:";
            if (applicationPath.startsWith(classpath)) {
                applicationPath = applicationPath.substring(applicationPath.indexOf(classpath) + classpath.length());
                resources.add(new ClassPathResource(applicationPath));
            } else {
                resources.add( new FileSystemResource(applicationPath));
            }
        }

        Resource[] array = new Resource[resources.size()];

        return resources.toArray(array);
    }

    /**
     * The bean registering the parsed applications
     * @return the bean of parsed applications
     */
    @Bean
    public ApplicationTemplate[] parsedApplications() {
        Converters.register(); // register all the converters

        Resource[] resources = getApplicationResources();
        try {
            InputStream[] inputStreams = new InputStream[resources.length];

            for (int i = 0; i < resources.length; i++) {
                inputStreams[i] = resources[i].getInputStream();
            }

            ApplicationTemplate[] applications = applicationParser.parse(inputStreams);

            log.info("{} application(s) loaded from resources {}", applications.length, Arrays.toString(resources));

            return applications;
        } catch (IOException ex) {
            throw new ApplicationParseException("Failed to parse application JSON", ex);
        }
    }
}
