package ie.ul.edward.ethics.applications.parsing.converters;

import ie.ul.edward.ethics.applications.parsing.components.ApplicationComponent;
import ie.ul.edward.ethics.applications.parsing.components.RadioQuestionComponent;
import ie.ul.edward.ethics.applications.parsing.components.SelectQuestionComponent;
import ie.ul.edward.ethics.applications.parsing.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This converter provides conversion of an object to a checkbox
 */
public class RadioQuestionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Set<String> keys = map.keySet();
        Set<String> requiredKeys = new TreeSet<>(List.of("title", "options"));
        Set<String> difference = new TreeSet<>(requiredKeys);
        difference.retainAll(keys);

        if (difference.size() != requiredKeys.size())
            throw new ApplicationParseException("The checkbox question component is missing keys");

        if (!List.class.isAssignableFrom(map.get("options").getClass()))
            throw new ApplicationParseException("The options field must map to a List");
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
    @SuppressWarnings("unchecked")
    public ApplicationComponent convert(Map<String, Object> map) throws ApplicationParseException {
        validate(map);
        List<SelectQuestionComponent.Option> options = new ArrayList<>();

        for (Object option : (List<?>)map.get("options")) {
            if (option instanceof Map) {
                Map<String, Object> optionMap = (Map<String, Object>)option;
                options.add(new SelectQuestionComponent.Option((String)optionMap.get("label"), (String)optionMap.get("value")));
            } else if (option instanceof String) {
                options.add(new SelectQuestionComponent.Option((String)option));
            } else {
                throw new ApplicationParseException("Unknown option type provided: " + option.getClass());
            }
        }

        return new RadioQuestionComponent((String)map.get("title"), (boolean)map.getOrDefault("required", false), options);
    }
}
