package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import ie.ul.ethics.scieng.applications.templates.components.SelectQuestionComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This abstract class provides a base class for any converters that during the parsing process,
 * convert SelectQuestionComponent.Option
 */
public abstract class OptionsConverter extends QuestionConverter {
    /**
     * Parse the checkbox option
     * @param option the option to parse
     * @return the parsed option
     */
    @SuppressWarnings("unchecked")
    protected SelectQuestionComponent.Option parseOption(Object option) {
        SelectQuestionComponent.Option parsed;
        String identifier = null;

        if (option instanceof Map) {
            Map<String, Object> optionMap = (Map<String, Object>)option;
            parsed = new SelectQuestionComponent.Option((String)optionMap.get("label"), (String)optionMap.get("value"));
            identifier = (String)optionMap.get("identifier");

            parsed.setId(ComponentConverter.parseDatabaseId(optionMap.getOrDefault("id", null)));
        } else if (option instanceof String) {
            parsed = new SelectQuestionComponent.Option((String)option);
        } else {
            throw new ApplicationParseException("Unknown option type provided: " + option.getClass());
        }

        if (identifier != null) {
            parsed.setIdentifier(identifier);
        }

        return parsed;
    }

    /**
     * Parse the options from the map
     * @param map the map representing the component
     * @return the list of parsed options
     */
    @SuppressWarnings("unchecked")
    protected List<SelectQuestionComponent.Option> parseOptions(Map<String, Object> map) {
        List<SelectQuestionComponent.Option> options = new ArrayList<>();

        for (Object option : (List<?>)map.get("options")) {
            options.add(parseOption(option));
        }

        return options;
    }
}
