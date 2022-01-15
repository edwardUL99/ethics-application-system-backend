package ie.ul.edward.ethics.applications.templates.config;

import ie.ul.edward.ethics.applications.templates.ApplicationParser;
import ie.ul.edward.ethics.applications.templates.ApplicationTemplate;
import ie.ul.edward.ethics.applications.templates.converters.Converters;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * The configuration class for application parsing
 */
@Configuration
@Log4j2
public class ParsingConfiguration {
    /**
     * The path to the application file
     */
    @Value("${applications.filePath:classpath:applications.json}")
    private String applicationPath;
    /**
     * The parser for parsing applications
     */
    private final ApplicationParser applicationParser;

    /**
     * Create a ParsingConfiguration object
     * @param applicationParser the parser for parsing applications
     */
    @Autowired
    public ParsingConfiguration(ApplicationParser applicationParser) {
        this.applicationParser = applicationParser;

        log.info("Using ApplicationParser {} to parse applications", applicationParser);
    }

    /**
     * Get the resource file for the application's file
     * @return the appropriate resource
     */
    private Resource getApplicationResource() {
        String classpath = "classpath:";
        if (applicationPath.startsWith(classpath)) {
            applicationPath = applicationPath.substring(applicationPath.indexOf(classpath) + classpath.length());
            return new ClassPathResource(applicationPath);
        } else {
            return new FileSystemResource(applicationPath);
        }
    }

    /**
     * The bean registering the parsed applications
     * @return the bean of parsed applications
     */
    @Bean
    public ApplicationTemplate[] parsedApplications() {
        Converters.register(); // register all the converters

        Resource resource = getApplicationResource();
        try {
            ApplicationTemplate[] applications = applicationParser.parse(resource.getInputStream());

            log.info("{} applications loaded from resource {}", applications.length, resource);

            return applications;
        } catch (IOException ex) {
            throw new ApplicationParseException("Failed to parse application JSON", ex);
        }
    }
}
