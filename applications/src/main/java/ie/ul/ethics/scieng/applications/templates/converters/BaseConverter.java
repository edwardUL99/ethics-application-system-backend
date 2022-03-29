package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;

import java.util.Map;

/**
 * This abstract class represents a base converter to convert components that are not QuestionComponents
 */
public abstract class BaseConverter implements ComponentConverter {
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

        ApplicationComponent component = this.createBase(map);

        String componentId = (String) map.get("componentId");

        if (componentId != null) {
            component.setComponentId(componentId);
        }

        component.setDatabaseId(ComponentConverter.parseDatabaseId(map.getOrDefault("databaseId", null)));

        return component;
    }

    /**
     * Create the base component to be converted. The convert method then does some additional field mapping
     * @param map the map to create the object from
     * @return the converted component
     * @throws ApplicationParseException if a parsing exception occurs
     */
    protected abstract ApplicationComponent createBase(Map<String, Object> map) throws ApplicationParseException;
}
