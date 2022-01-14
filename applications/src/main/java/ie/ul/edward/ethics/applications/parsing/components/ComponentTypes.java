package ie.ul.edward.ethics.applications.parsing.components;

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
    public static final String REPLACEMENT_BRANCH = "replacement-branch";
    /**
     * This type is used for action branches
     */
    public static final String ACTION_BRANCH = "action-branch";
    /**
     * This type is used for question branches
     */
    public static final String QUESTION_BRANCH = "question-branch";
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
}
