package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

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

    /**
     * Parse the database ID
     * @param number the database ID object to convert
     * @return the converted database ID
     */
    static Long parseDatabaseId(Object number) {
        if (number instanceof Long) {
            return (Long) number;
        } else if (number instanceof Integer) {
            return ((Integer)number).longValue();
        } else {
            return null;
        }
    }
}
