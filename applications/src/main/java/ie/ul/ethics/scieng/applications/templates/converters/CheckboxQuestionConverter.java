package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import ie.ul.ethics.scieng.applications.templates.components.*;

import java.util.*;

/**
 * This converter provides conversion of an object to a checkbox
 */
@Converter(ComponentType.CHECKBOX_QUESTION)
public class CheckboxQuestionConverter extends OptionsConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.CHECKBOX_QUESTION, map.keySet(), "title", "name", "options");

        if (!List.class.isAssignableFrom(map.get("options").getClass()))
            throw new ApplicationParseException("The options field must map to a List");
    }

    /**
     * Create the base question component to be converted. The convert method then does some additional field mapping
     *
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    @Override
    protected QuestionComponent createBase(Map<String, Object> map) throws ApplicationParseException {
        List<SelectQuestionComponent.Option> options = parseOptions(map);

        return new CheckboxQuestionComponent((String)map.get("title"), (String)map.get("name"),
                Converters.parseLongString(ComponentType.CHECKBOX_QUESTION, "description", map.getOrDefault("description", null)),
                (boolean)map.getOrDefault("required", QuestionComponent.DEFAULT_REQUIRED), options,
                (boolean)map.getOrDefault("inline", false));
    }
}
