package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.ContainerComponent;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;

import java.util.*;

/**
 * This class represents a converter to convert a map to a container component
 */
@Converter(ComponentType.CONTAINER)
public class ContainerConverter extends BaseConverter {
    /**
     * Validates the map for conversion
     *
     * @param map the map to validate
     * @throws ApplicationParseException if validation fails
     */
    @Override
    public void validate(Map<String, Object> map) throws ApplicationParseException {
        Converters.validateKeys(ComponentType.CONTAINER, map.keySet(), "id", "components");

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

        for (Map<String, Object> sub : (List<Map<String, Object>>)map.get("components"))
            subComponents.add(Converters.getConverter((String)sub.get("type")).convert(sub));

        return new ContainerComponent((String)map.get("id"), subComponents);
    }
}
