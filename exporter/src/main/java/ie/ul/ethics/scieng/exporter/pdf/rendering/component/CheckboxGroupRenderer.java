package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.CheckboxGroupComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;
import ie.ul.ethics.scieng.exporter.pdf.rendering.AnswerRenderer;
import ie.ul.ethics.scieng.exporter.pdf.rendering.OptionsAnswerRenderer;

import java.util.List;
import java.util.Objects;

/**
 * A renderer for checkbox groups
 */
public class CheckboxGroupRenderer extends QuestionComponentRenderer {
    /**
     * The checkbox component
     */
    private final CheckboxGroupComponent component;

    /**
     * Construct a component instance
     *
     * @param application the application being rendered
     * @param component   the component to render
     * @param context     rendering context
     */
    public CheckboxGroupRenderer(Application application, QuestionComponent component, PDFContext context) {
        super(application, component, context);
        this.component = (CheckboxGroupComponent) component;
    }

    /**
     * A hook to define a custom answer render for the renderer. Return null to not define a custom one
     *
     * @param answer the answer being rendered
     * @return the custom renderer, null if not
     */
    @Override
    protected AnswerRenderer customAnswerRenderer(Answer answer) {
        return new CheckboxAnswerRenderer(component);
    }

    /**
     * A renderer for checkbox group answers. It is a specialisation of the options answer renderer
     */
    static class CheckboxAnswerRenderer extends OptionsAnswerRenderer {
        /**
         * The component the answer is being rendered for
         */
        private final CheckboxGroupComponent component;

        /**
         * Create a renderer instance
         * @param component the component the answer is being rendered for
         */
        CheckboxAnswerRenderer(CheckboxGroupComponent component) {
            this.component = component;
        }

        /**
         * Parses the value in the options answer to a value to display in the answer
         *
         * @param value the value to render
         * @return the answer value
         */
        @Override
        protected String parseOptionValue(String value) {
            value = super.parseOptionValue(value); // parse the value in case it is identifier=value
            List<CheckboxGroupComponent.Checkbox> checkboxes = component.getCheckboxes();

            for (CheckboxGroupComponent.Checkbox checkbox : checkboxes) {
                String identifier = checkbox.getIdentifier();

                if (identifier != null && Objects.equals(identifier, value)) {
                    String title = checkbox.getTitle();

                    return (title == null) ? identifier:title;
                }
            }

            return value;
        }
    }
}
