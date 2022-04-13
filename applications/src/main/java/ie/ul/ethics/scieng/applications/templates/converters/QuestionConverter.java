package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;

import java.util.Map;

/**
 * An abstract base class for all question converters
 */
public abstract class QuestionConverter implements ComponentConverter {
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
        this.validate(map);
        QuestionComponent component = this.createBase(map);
        component.setEditable((boolean)map.getOrDefault("editable", true));
        component.setAutofill((String)map.getOrDefault("autofill", null));
        component.setRequestInput((boolean)map.getOrDefault("requestInput", false));

        String componentId = (String) map.get("componentId");

        if (componentId != null)
            component.setComponentId(componentId);

        component.setDatabaseId(ComponentConverter.parseDatabaseId(map.getOrDefault("databaseId", null)));

        return component;
    }

    /**
     * Create the base question component to be converted. The convert method then does some additional field mapping
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    protected abstract QuestionComponent createBase(Map<String, Object> map) throws ApplicationParseException;
}
