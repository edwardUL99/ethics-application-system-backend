package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.TextComponent;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.Map;

/**
 * This converter converts the map to a text component
 */
@Converter(ComponentType.TEXT)
public class TextConverter extends BaseConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.TEXT, map.keySet(), "title", "content");
    }

    /**
     * Create the base component to be converted. The convert method then does some additional field mapping
     *
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    @Override
    protected ApplicationComponent createBase(Map<String, Object> map) throws ApplicationParseException {
        Object contentObj = map.get("content");
        String content = Converters.parseLongString(ComponentType.TEXT, "content", contentObj);

        return new TextComponent((String)map.get("title"), content, (boolean)map.getOrDefault("nested", false));
    }
}
