package ie.ul.ethics.scieng.applications.templates.components;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * This enum value represents a component type for an application
 */
public enum ComponentType {
    /**
     * This is the type used for text components
     */
    TEXT("text"),
    /**
     * This is the type used for choice question components
     */
    SELECT_QUESTION("select-question"),
    /**
     * This is the type used for text answer question components
     */
    TEXT_QUESTION("text-question"),
    /**
     * This is the type used for container components
     */
    CONTAINER("container"),
    /**
     * This is the type used for section components
     */
    SECTION("section"),
    /**
     * This type is used for components to gather a signature
     */
    SIGNATURE("signature"),
    /**
     * This type is used for replacement branches
     */
    REPLACEMENT_BRANCH("replacement"),
    /**
     * This type is used for action branches
     */
    ACTION_BRANCH("action"),
    /**
     * This type is used for question branches
     */
    QUESTION_BRANCH("question"),
    /**
     * This type is used for a question that is a checkbox
     */
    CHECKBOX_QUESTION("checkbox-question"),
    /**
     * This type is used for when a question provides options as a radio (one and one answer only)
     */
    RADIO_QUESTION("radio-question"),
    /**
     * This type is used for a question that is multipart
     */
    MULTIPART_QUESTION("multipart-question"),
    /**
     * This type is used for a component that is a group of checkboxes which executes a default branch
     */
    CHECKBOX_GROUP("checkbox-group"),
    /**
     * This type is used for a component that contains columns and rows of inputs giving answers to those inputs
     */
    QUESTION_TABLE("question-table");
    /**
     * The label for the component to serialize
     */
    private final String label;

    /**
     * Create the enum value
     * @param label the label for the component
     */
    ComponentType(String label) {
        this.label = label;
    }

    /**
     * Get the label of the component type
     * @return label
     */
    @JsonValue
    public String label() {
        return label;
    }

    /**
     * Get the component type for the given label
     * @param label the label of the component
     * @return the matching enum value
     * @throws IllegalArgumentException if no value matches label
     */
    public static ComponentType of(String label) {
        if (label == null)
            return null;

        return Arrays.stream(values())
                .filter(e -> e.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum value for label " + label));
    }

    /**
     * Return the string representation
     * @return string representation
     */
    public String toString() {
        return label;
    }

}
