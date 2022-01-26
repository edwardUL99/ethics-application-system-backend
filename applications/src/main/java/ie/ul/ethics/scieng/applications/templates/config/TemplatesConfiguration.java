package ie.ul.ethics.scieng.applications.templates.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ie.ul.ethics.scieng.applications.templates.ApplicationParser;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;
import ie.ul.ethics.scieng.applications.templates.ComponentDeserializer;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.converters.Converters;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

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

        Converters.register(); // register all the converters
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
     * The bean used to load application templates
     * @return the template loader
     */
    @Bean
    public ApplicationTemplateLoader applicationTemplateLoader() {
        Resource[] resources = getApplicationResources();
        ApplicationTemplateLoader loader = new ApplicationTemplateLoader(Arrays.asList(resources), applicationParser);
        ApplicationTemplate[] applications = loader.loadTemplates();

        log.info("{} application(s) loaded from resources {}", applications.length, Arrays.toString(resources));

        return loader;
    }

    /**
     * Create the module for deserializing application components
     * @return the application components module
     */
    @Bean
    public Module applicationComponentModule() {
        ComponentDeserializer deserializer = new ComponentDeserializer();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ApplicationComponent.class, deserializer);

        log.info("Registering custom JSON Deserializer {} to deserialize application components", deserializer);

        return module;
    }
}
