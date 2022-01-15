package ie.ul.edward.ethics.applications.templates.converters;

import ie.ul.edward.ethics.applications.templates.components.ComponentTypes;
import ie.ul.edward.ethics.applications.templates.exceptions.ApplicationParseException;
import lombok.extern.log4j.Log4j2;
import org.reflections.Reflections;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

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
    private static final Map<String, ComponentConverter> converters = new HashMap<>();

    /**
     * Register all Converter annotated classes found in the ie.ul.edward.ethics.applications.parsing.templates package
     * @throws ApplicationParseException if the converters fail to be registered
     */
    public static void register() throws ApplicationParseException {
        Reflections reflections = new Reflections("ie.ul.edward.ethics.applications.templates.converters");

        for (Class<?> annotated : reflections.get(TypesAnnotated.with(Converter.class).asClass())) {
            if (!ComponentConverter.class.isAssignableFrom(annotated))
                throw new ApplicationParseException("You cannot annotate a class that does not implement ComponentConverter with the Converter annotation");

            Converter annotation = annotated.getAnnotation(Converter.class);
            String componentType = annotation.value();

            if (!ComponentTypes.isValidComponentType(componentType))
                throw new ApplicationParseException("The class " + ComponentTypes.class.getName() + " does not contain component type " + componentType);

            try {
                log.debug("Registering ComponentConverter {} for ComponentType {}", annotated.getName(), componentType);
                converters.put(componentType, (ComponentConverter) annotated.getDeclaredConstructor().newInstance());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        log.info("Registered {} ComponentConverters successfully", converters.size());
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
