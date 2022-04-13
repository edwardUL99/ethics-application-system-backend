package ie.ul.ethics.scieng.common.properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

/**
 * This class provides utilities for retrieving properties from a specified order
 */
public final class PropertyFinder {
    /**
     * The set of sources in the order registered
     */
    private static final Set<PropertySource> sources = new LinkedHashSet<>();

    /**
     * Register the property source. Call this for all property sources in the order you wish to find the properties in first
     * @param propertySource the source to register
     */
    public static void registerSource(PropertySource propertySource) {
        sources.add(propertySource);
    }

    /**
     * Set the loaded properties to use for resolution from property files
     * @param loadedProperties the loaded property file resources
     */
    public static void configure(LoadedProperties loadedProperties) {
        PropertyFileResolver.loadedProperties = loadedProperties;
    }

    /**
     * Find the property in the registered sources, in the order they were registered by {@link #registerSource(PropertySource)},
     * with the first property being found being the one returned
     * @param propertyNames a single property name (or array of the multiple variants the property can be set as)
     * @return the property if found, null if not
     */
    public static String findProperty(String...propertyNames) {
        String property;

        for (String propertyName : propertyNames) {
            for (PropertySource source : sources) {
                property = source.propertyResolver.resolve(propertyName);

                if (property != null)
                    return property;
            }
        }

        return null;
    }

    /**
     * This enum represents th
     */
    public enum PropertySource {
        /**
         * Find the property in the system properties
         */
        SYSTEM(new SystemPropertyResolver()),
        /**
         * Find the property in the environment variables
         */
        ENVIRONMENT(new EnvironmentPropertyResolver()),
        /**
         * Find the property in the ethics properties files
         */
        PROPERTY_FILES(new PropertyFileResolver());

        /**
         * The property resolver interface for this enum value
         */
        private final PropertyResolver propertyResolver;

        /**
         * Create a property source enum value
         * @param propertyResolver the resolver for the property
         */
        PropertySource(PropertyResolver propertyResolver) {
            this.propertyResolver = propertyResolver;
        }
    }

    /**
     * This interface resolves the given property
     */
    private interface PropertyResolver {
        /**
         * Resolve the property by propertyName
         * @param propertyName the name of the property
         * @return the resolved property if found, else null
         */
        String resolve(String propertyName);
    }

    /**
     * Implements the resolver interface for resolving from system properties
     */
    private static class SystemPropertyResolver implements PropertyResolver {
        /**
         * Resolve the property by propertyName
         *
         * @param propertyName the name of the property
         * @return the resolved property if found, else null
         */
        @Override
        public String resolve(String propertyName) {
            return System.getProperty(propertyName);
        }
    }

    /**
     * Implements the resolver interface for resolving from environment variables
     */
    private static class EnvironmentPropertyResolver implements PropertyResolver {
        /**
         * Resolve the property by propertyName
         *
         * @param propertyName the name of the property
         * @return the resolved property if found, else null
         */
        @Override
        public String resolve(String propertyName) {
            return System.getenv(propertyName);
        }
    }

    /**
     * Attempts to read the property from the ethics properties classpath using spring utilities
     */
    private static class PropertyFileResolver implements PropertyResolver {
        /**
         * The loaded properties. Needs to be configured by {@link PropertyFinder#configure(LoadedProperties)}
         */
        private static LoadedProperties loadedProperties;

        /**
         * Resolve the property by propertyName
         *
         * @param propertyName the name of the property
         * @return the resolved property if found, else null
         */
        @Override
        public String resolve(String propertyName) {
            if (loadedProperties != null) {
                try {
                    for (Resource resource : loadedProperties.getResources()) {
                        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
                        String property = properties.getProperty(propertyName);

                        if (property != null)
                            return property;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

            return null;
        }
    }
}
