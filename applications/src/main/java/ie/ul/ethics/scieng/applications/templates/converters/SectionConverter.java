package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.SectionComponent;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This converter converts a map to a Section component
 */
@Converter(ComponentType.SECTION)
public class SectionConverter extends BaseConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.SECTION, map.keySet(), "title", "components");

        if (!List.class.isAssignableFrom(map.get("components").getClass()))
            throw new ApplicationParseException("components is expected to be a list but it is not");
    }

    /**
     * Create the base component to be converted. The convert method then does some additional field mapping
     *
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    protected ApplicationComponent createBase(Map<String, Object> map) throws ApplicationParseException {
        List<ApplicationComponent> subComponents = new ArrayList<>();

        for (Map<String, Object> sub : (List<Map<String, Object>>)map.get("components")) {
            String type = (String) sub.get("type");

            if (type.equals(ComponentType.TEXT.label()))
                sub.put("nested", true);

            subComponents.add(Converters.getConverter(type).convert(sub));
        }

        return new SectionComponent((String)map.get("title"),
                Converters.parseLongString(ComponentType.SECTION, "description", map.getOrDefault("description", null)), subComponents,
                (boolean)map.getOrDefault("autoSave", true));
    }
}
