package ie.ul.ethics.scieng.app.config;

import ie.ul.ethics.scieng.common.properties.LoadedProperties;
import ie.ul.ethics.scieng.common.properties.PropertyFinder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * This class configures the property file reading for each ethics module
 */
@Configuration
@Log4j2
@Order(-1000)
public class PropertiesConfig {
    /**
     * The loaded property resources
     */
    private static Resource[] resources;

    /**
     * Create an instance
     * @param loadedProperties holds the loaded resources
     */
    @Autowired
    public PropertiesConfig(LoadedProperties loadedProperties) {
        loadedProperties.setResources(resources);
        PropertyFinder.configure(loadedProperties);
    }

    /**
     * Create the configurer for property sources and return it as a bean
     * @return the configurer bean
     * @throws IOException if an error occurs reading resources
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath*:*.ethics.properties");

        log.info("Configured application property sources (*.ethics.properties) using files found on the classpath. The " +
                "files found are: {}", (Object)resources);

        PropertiesConfig.resources = resources;
        configurer.setLocations(resources);

        return configurer;
    }
}
