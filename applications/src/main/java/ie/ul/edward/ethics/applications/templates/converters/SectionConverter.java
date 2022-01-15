package ie.ul.edward.ethics.applications.templates.converters;

import ie.ul.edward.ethics.applications.templates.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.templates.components.ComponentTypes;
import ie.ul.edward.ethics.applications.templates.components.SectionComponent;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This converter converts a map to a Section component
 */
@Converter(ComponentTypes.SECTION)
public class SectionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Set<String> keys = map.keySet();
        Set<String> requiredKeys = new TreeSet<>(List.of("type", "title", "description", "components"));
        Set<String> difference = new TreeSet<>(requiredKeys);
        difference.retainAll(keys);

        if (difference.size() != requiredKeys.size())
            throw new ApplicationParseException("The section component is missing keys");

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

        for (Map<String, Object> sub : (List<Map<String, Object>>)map.get("components"))
            subComponents.add(Converters.getConverter((String)sub.get("type")).convert(sub));

        return new SectionComponent((String)map.get("title"), (String)map.get("description"), subComponents);
    }
}
