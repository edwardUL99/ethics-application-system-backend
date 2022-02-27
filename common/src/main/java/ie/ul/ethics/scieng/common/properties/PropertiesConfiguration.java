package ie.ul.ethics.scieng.common.properties;

import org.springframework.context.annotation.Configuration;

/**
 * This configuration initialises the properties configuration
 */
@Configuration
public class PropertiesConfiguration {
    static {
        for (PropertyFinder.PropertySource source : PropertyFinder.PropertySource.values()) {
            PropertyFinder.registerSource(source);
        }
    }
}
