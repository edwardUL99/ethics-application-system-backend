package ie.ul.edward.ethics.applications.templates.converters;

import ie.ul.edward.ethics.applications.templates.components.*;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This converter provides conversion of an object to a checkbox
 */
@Converter(ComponentTypes.CHECKBOX_QUESTION)
public class CheckboxQuestionConverter implements ComponentConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentTypes.CHECKBOX_QUESTION, map.keySet(), "title", "name", "options");

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

        return new CheckboxQuestionComponent((String)map.get("title"), (String)map.get("name"),
                Converters.parseLongString(ComponentTypes.CHECKBOX_QUESTION, "description", map.getOrDefault("description", null)),
                (boolean)map.getOrDefault("required", QuestionComponent.DEFAULT_REQUIRED), options);
    }
}
