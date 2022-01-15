package ie.ul.edward.ethics.applications.templates.components;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This component represents a component that has options to choose from as the answer
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class SelectQuestionComponent extends QuestionComponent {
    /**
     * True if multiple answers are allowed
     */
    private boolean multiple;
    /**
     * The list of options for this question
     */
    private List<Option> options;
    /**
     * Determines if this question should add a field to allow the specification of another answer that's not provided
     */
    private boolean addOther;

    /**
     * Create a default select question component
     */
    public SelectQuestionComponent() {
        this(null, null, null, DEFAULT_REQUIRED, false, new ArrayList<>(), false);
    }

    /**
     * Create a SelectQuestionComponent
     * @param title the title of the question
     * @param name the name to give to the question
     * @param description the description of the question
     * @param required true if the question requires an answer, false if not
     * @param multiple true if multiple responses are allowed
     * @param options the list of options to add to the question
     * @param addOther true if an 'Other' text field should be added
     */
    public SelectQuestionComponent(String title, String name, String description, boolean required, boolean multiple, List<Option> options, boolean addOther) {
        super(ComponentTypes.SELECT_QUESTION, title, name, description, required);
        this.multiple = multiple;
        this.options = options;
        this.addOther = addOther;
    }

    /**
     * This class represents an option for the select question
     */
    @Getter
    @Setter
    @EqualsAndHashCode
    public static class Option {
        /**
         * The label for the option to display
         */
        private String label;
        /**
         * The value to send in the post request
         */
        private String value;

        /**
         * Create an Option where the value is the same as the label
         * @param value the value to use
         */
        public Option(String value) {
            this(value, value);
        }

        /**
         * Create an Option with the provided label and value
         * @param label the label value
         * @param value the value to send in the request
         */
        public Option(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }
}
