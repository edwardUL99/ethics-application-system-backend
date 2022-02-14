package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;
import ie.ul.ethics.scieng.applications.templates.components.SelectQuestionComponent;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This class implements a converter for a SelectQuestionComponent
 */
@Converter(ComponentType.SELECT_QUESTION)
public class SelectQuestionConverter extends QuestionConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.SELECT_QUESTION, map.keySet(), "title", "name", "multiple", "options");

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
    @SuppressWarnings("unchecked")
    protected QuestionComponent createBase(Map<String, Object> map) throws ApplicationParseException {
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

        return new SelectQuestionComponent((String)map.get("title"), (String)map.get("name"),
                Converters.parseLongString(ComponentType.SELECT_QUESTION, "description", map.getOrDefault("description", null)),
                (boolean)map.getOrDefault("required", QuestionComponent.DEFAULT_REQUIRED),
                (boolean)map.get("multiple"), options, (boolean)map.getOrDefault("addOther", false));
    }
}
