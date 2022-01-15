package ie.ul.edward.ethics.applications.templates.converters;

import ie.ul.edward.ethics.applications.templates.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.templates.components.ComponentTypes;
import ie.ul.edward.ethics.applications.templates.components.TextComponent;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;

import java.util.Map;

/**
 * This converter converts the map to a text component
 */
@Converter(ComponentTypes.TEXT)
public class TextConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentTypes.TEXT, map.keySet(), "title", "content");
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

        Object contentObj = map.get("content");
        String content = Converters.parseLongString(ComponentTypes.TEXT, "content", contentObj);

        return new TextComponent((String)map.get("title"), content);
    }
}
