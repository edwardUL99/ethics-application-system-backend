package ie.ul.ethics.scieng.applications.templates.converters;

import ie.ul.ethics.scieng.applications.templates.components.ComponentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to register a ComponentConverter.
 *
 * Any classes in this package that implements {@link ComponentConverter} and annotated with this annotation, will be
 * picked up by {@link Converters}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Converter {
    /**
     * The component type the converter is to be registered for. The type must be a public static final field in {@link ie.ul.ethics.scieng.applications.templates.components.ComponentTypes}
     * @return the component type
     */
    ComponentType value();
}
