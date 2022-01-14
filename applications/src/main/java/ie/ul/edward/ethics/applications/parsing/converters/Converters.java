package ie.ul.edward.ethics.applications.parsing.converters;

import ie.ul.edward.ethics.applications.parsing.components.ComponentTypes;
import ie.ul.edward.ethics.applications.parsing.exceptions.ApplicationParseException;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides utilities for registering and retrieving converters
 */
public final class Converters {
    /**
     * The mapping of converters
     */
    private static final Map<String, ComponentConverter> converters = new HashMap<>();

    static {
        converters.put(ComponentTypes.TEXT, new TextConverter());
        converters.put(ComponentTypes.SECTION, new SectionConverter());
        converters.put(ComponentTypes.CONTAINER, new ContainerConverter());
        converters.put(ComponentTypes.TEXT_QUESTION, new TextQuestionConverter());
        converters.put(ComponentTypes.SELECT_QUESTION, new SelectQuestionConverter());
        converters.put(ComponentTypes.MULTIPART_QUESTION, new MultipartQuestionConverter());
        converters.put(ComponentTypes.CHECKBOX_GROUP, new CheckboxGroupConverter());
        converters.put(ComponentTypes.SIGNATURE, new SectionConverter());
        converters.put(ComponentTypes.RADIO_QUESTION, new RadioQuestionConverter());
        converters.put(ComponentTypes.CHECKBOX_QUESTION, new CheckboxQuestionConverter());
    }

    /**
     * Retrieve the converter for the provided type
     * @param type the type of component
     * @return the converter
     * @throws ApplicationParseException if it cannot be found
     */
    public static ComponentConverter getConverter(String type) throws ApplicationParseException {
        type = type.toLowerCase();
        ComponentConverter converter = converters.get(type);

        if (converter == null)
            throw new ApplicationParseException("The application does not know how to convert a component of type " + type);

        return converter;
    }
}
