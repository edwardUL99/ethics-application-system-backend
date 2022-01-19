package ie.ul.edward.ethics.applications.templates.converters;

import ie.ul.edward.ethics.applications.templates.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.templates.components.ComponentType;
import ie.ul.edward.ethics.applications.templates.components.SignatureQuestionComponent;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;

import java.util.Map;

/**
 * This class converts maps into signature questions
 */
@Converter(ComponentType.SIGNATURE)
public class SignatureQuestionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.SIGNATURE, map.keySet(), "title", "name", "label");
    }

    /**
     * Convert the provided map to the equivalent ApplicationComponent.
     * Should call the validate method to ensure the map is valid
     *
     * @param map the map to convert
     * @return the equivalent application component
     * @throws ApplicationParseException if the map isn't valid or an error occurs
     */
    @Override
    public ApplicationComponent convert(Map<String, Object> map) throws ApplicationParseException {
        validate(map);

        return new SignatureQuestionComponent((String)map.get("title"), (String)map.get("name"),
                Converters.parseLongString(ComponentType.SIGNATURE, "description", map.getOrDefault("description", null)),
                (String)map.get("label"));
    }
}
