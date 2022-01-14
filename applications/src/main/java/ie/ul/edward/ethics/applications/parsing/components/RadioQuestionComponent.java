package ie.ul.edward.ethics.applications.parsing.components;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * This component represents a question that has radios that can have only one answe
 */
@EqualsAndHashCode(callSuper = false)
public class RadioQuestionComponent extends SelectQuestionComponent {
    /**
     * Create a default RadioQuestionComponent
     */
    public RadioQuestionComponent() {
        this(null, false, new ArrayList<>());
    }

    /**
     * Create a RadioQuestionComponent
     * @param title the title of the component
     * @param required true if required or not
     * @param options the options for the checkbox
     */
    public RadioQuestionComponent(String title, boolean required, List<SelectQuestionComponent.Option> options) {
        super(title, null, required, false, options, false);
        setType(ComponentTypes.RADIO_QUESTION);
    }
}
