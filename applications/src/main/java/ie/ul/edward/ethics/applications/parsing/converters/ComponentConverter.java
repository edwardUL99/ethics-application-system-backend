package ie.ul.edward.ethics.applications.parsing.converters;

import ie.ul.edward.ethics.applications.parsing.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.parsing.exceptions.ApplicationParseException;

import java.util.Map;

/**
 * This interface represents a converter that can convert a JSON map to a component
 */
public interface ComponentConverter {
    /**
     * Validates the map for conversion
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    void validate(Map<String, Object> map) throws ApplicationParseException;

    /**
     * Convert the provided map to the equivalent ApplicationComponent.
     * Should call the validate method to ensure the map is valid
     * @param map the map to convert
     * @return the equivalent application component
     * @throws ApplicationParseException if the map isn't valid or an error occurs
     */
    ApplicationComponent convert(Map<String, Object> map) throws ApplicationParseException;
}
