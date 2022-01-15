package ie.ul.edward.ethics.applications.templates.components;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class holds constants representing the available component types
 */
public final class ComponentTypes {
    /**
     * This is the type used for text components
     */
    public static final String TEXT = "text";
    /**
     * This is the type used for choice question components
     */
    public static final String SELECT_QUESTION = "select-question";
    /**
     * This is the type used for text answer question components
     */
    public static final String TEXT_QUESTION = "text-question";
    /**
     * This is the type used for container components
     */
    public static final String CONTAINER = "container";
    /**
     * This is the type used for section components
     */
    public static final String SECTION = "section";
    /**
     * This type is used for components to gather a signature
     */
    public static final String SIGNATURE = "signature";
    /**
     * This type is used for replacement branches
     */
    public static final String REPLACEMENT_BRANCH = "replacement";
    /**
     * This type is used for action branches
     */
    public static final String ACTION_BRANCH = "action";
    /**
     * This type is used for question branches
     */
    public static final String QUESTION_BRANCH = "question";
    /**
     * This type is used for a question that is a checkbox
     */
    public static final String CHECKBOX_QUESTION = "checkbox-question";
    /**
     * This type is used for when a question provides options as a radio (one and one answer only)
     */
    public static final String RADIO_QUESTION = "radio-question";
    /**
     * This type is used for a question that is multipart
     */
    public static final String MULTIPART_QUESTION = "multipart-question";
    /**
     * This type is used for a component that is a group of checkboxes which executes a default branch
     */
    public static final String CHECKBOX_GROUP = "checkbox-group";
    /**
     * List of all the registered types in this class
     */
    private static final List<String> COMPONENT_TYPES = new ArrayList<>();

    static {
        ComponentTypes types = new ComponentTypes();

        COMPONENT_TYPES.addAll(Arrays.stream(ComponentTypes.class.getDeclaredFields())
                .filter(f -> Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && String.class.isAssignableFrom(f.getType()))
                .map(f -> {
                    try {
                        return (String) f.get(types);
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    /**
     * Determines if the provided type is a registered type
     * @param type the type to check
     * @return true if registered in this class, false if not
     */
    public static boolean isValidComponentType(String type) {
        return COMPONENT_TYPES.contains(type);
    }
}
