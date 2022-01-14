package ie.ul.edward.ethics.applications.parsing.components;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * This component represents a question that has checkboxes that can have multiple answers
 */
@EqualsAndHashCode(callSuper = false)
public class CheckboxQuestionComponent extends SelectQuestionComponent {
    /**
     * Create a default CheckboxQuestionComponent
     */
    public CheckboxQuestionComponent() {
        this(null, false, new ArrayList<>());
    }

    /**
     * Create a CheckboxQuestionComponent
     * @param title the title of the component
     * @param required true if required or not
     * @param options the options for the checkbox
     */
    public CheckboxQuestionComponent(String title, boolean required, List<Option> options) {
        super(title, null, required, true, options, false);
        setType(ComponentTypes.CHECKBOX_QUESTION);
    }
}
