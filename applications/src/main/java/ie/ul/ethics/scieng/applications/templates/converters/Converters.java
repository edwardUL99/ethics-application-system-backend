package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.exceptions.ApplicationParseException;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;

import java.util.*;

import static org.reflections.scanners.Scanners.TypesAnnotated;

/**
 * This class provides utilities for registering and retrieving converters.
 * {@link #register()} should be called before using {@link #getConverter(String)} or else the class will not know about
 * any {@link Converter} annotated classes
 */
@Log4j2
public final class Converters {
    /**
     * The mapping of converters
     */
    private static final Map<ComponentType, ComponentConverter> converters = new HashMap<>();

    /**
     * Register all Converter annotated classes found in the ie.ul.ethics.scieng.applications.parsing.templates package
     * @throws ApplicationParseException if the converters fail to be registered
     */
    public static void register() throws ApplicationParseException {
        Reflections reflections = new Reflections("ie.ul.ethics.scieng.applications.templates.converters");

        for (Class<?> annotated : reflections.get(TypesAnnotated.with(Converter.class).asClass())) {
            if (!ComponentConverter.class.isAssignableFrom(annotated))
                throw new ApplicationParseException("You cannot annotate a class that does not implement ComponentConverter with the Converter annotation");

            Converter annotation = annotated.getAnnotation(Converter.class);
            ComponentType componentType = annotation.value();

            try {
                log.debug("Registering ComponentConverter {} for ComponentType {}", annotated.getName(), componentType);
                converters.put(componentType, (ComponentConverter) annotated.getDeclaredConstructor().newInstance());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        log.info("Registered {} Application Template ComponentConverters successfully", converters.size());
    }

    /**
     * Retrieve the converter for the provided type
     * @param type the type of component
     * @return the converter
     * @throws ApplicationParseException if it cannot be found
     */
    public static ComponentConverter getConverter(String type) throws ApplicationParseException {
        if (type == null)
            throw new ApplicationParseException("A null type value has been passed into Converters#getConverter. " +
                    "Has a key type with the component type been defined in the JSON component object?");

        type = type.toLowerCase();
        ComponentConverter converter = converters.get(ComponentType.of(type));

        if (converter == null)
            throw new ApplicationParseException("The application does not know how to convert a component of type " + type);

        return converter;
    }

    /**
     * Validate that the provided keys provide all the required keys
     * @param componentType the type of the component being validated
     * @param keys the keys to validate
     * @param required the required keys
     * @throws ApplicationParseException if required keys are missing
     */
    public static void validateKeys(ComponentType componentType, Set<String> keys, String...required) throws ApplicationParseException {
        Set<String> requiredKeys = new TreeSet<>(List.of(required));
        Set<String> difference = new TreeSet<>(requiredKeys);
        difference.retainAll(keys);

        if (difference.size() != requiredKeys.size())
            throw new ApplicationParseException("The " + componentType.label() + " component is missing keys, required keys are: " + requiredKeys);
    }

    /**
     * This method parses a string that may be a single string or broken into an array and needs concatenation
     * @param componentType the component type being converted
     * @param field the name of the field being parsed
     * @param string the string to process
     */
    @SuppressWarnings("unchecked")
    public static String parseLongString(ComponentType componentType, String field, Object string) {
        if (string == null)
            return null;

        String text;
        if (string instanceof String) {
            text = (String)string;
        } else if (string instanceof List) {
            StringBuilder builder = new StringBuilder();

            for (String s : (List<String>)string)
                builder.append(s);

            text = builder.toString();
        } else {
            if (componentType != null)
                throw new ApplicationParseException("Illegal value of the " + field + " field in the " + componentType.label() + " element. The only allowed types is" +
                    " a single string or an array of strings");
            else
                throw new ApplicationParseException("Illegal value of the " + field + ". The only allowed types is" +
                        " a single string or an array of strings");
        }

        return text;
    }
}
