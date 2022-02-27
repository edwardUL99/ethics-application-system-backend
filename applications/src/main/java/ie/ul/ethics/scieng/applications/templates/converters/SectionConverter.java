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
public class SectionConverter implements ComponentConverter {
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
     * Convert the provided map to the equivalent ApplicationComponent
     *
     * @param map the map to convert
     * @return the equivalent application component
     * @throws ApplicationParseException if the map isn't valid or an error occurs
     */
    @Override
    @SuppressWarnings("unchecked")
    public ApplicationComponent convert(Map<String, Object> map) throws ApplicationParseException {
        validate(map);
        List<ApplicationComponent> subComponents = new ArrayList<>();

        for (Map<String, Object> sub : (List<Map<String, Object>>)map.get("components")) {
            String type = (String) sub.get("type");

            if (type.equals(ComponentType.TEXT.label())) {
                sub.put("nested", true);
            }

            subComponents.add(Converters.getConverter((String) sub.get("type")).convert(sub));
        }

        SectionComponent component = new SectionComponent((String)map.get("title"),
                Converters.parseLongString(ComponentType.SECTION, "description", map.getOrDefault("description", null)), subComponents,
                (boolean)map.getOrDefault("autoSave", true));

        String componentId = (String) map.get("componentId");

        if (componentId != null) {
            component.setComponentId(componentId);
        }

        return component;
    }
}
