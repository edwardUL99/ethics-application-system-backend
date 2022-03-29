package ie.ul.ethics.scieng.exporter.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;

/**
 * This is the implementation of the renderer for questions
 */
public class QuestionComponentRenderer implements ComponentRenderer {
    /**
     * The application being rendered to PDF
     */
    protected final Application application;
    /**
     * The component being rendered
     */
    protected final QuestionComponent component;

    /**
     * Construct a component instance
     * @param application the application being rendered
     * @param component the component to render
     */
    public QuestionComponentRenderer(Application application, QuestionComponent component) {
        this.application = application;
        this.component = component;
    }

    /**
     * Render the component into a PDF element
     *
     * @return the rendered element
     */
    @Override
    public Element renderToElement() {
        Paragraph paragraph = new Paragraph();

        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK);
        Chunk title = new Chunk(component.getTitle(), font);
        paragraph.add(title);

        String descriptionValue = component.getDescription();

        if (descriptionValue != null && !descriptionValue.isEmpty()) {
            font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.LIGHT_GRAY);
            Chunk description = new Chunk(descriptionValue, font);
            paragraph.add(description);
        }

        Answer answer = application.getAnswers().get(component.getComponentId());
        paragraph.add(renderAnswer(answer));

        return paragraph;
    }

    /**
     * Render the answer of the component
     * @param answer the answer to render
     * @return the rendered answer
     */
    protected Element renderAnswer(Answer answer) {
        if (answer != null) {
            AnswerRenderer renderer = AnswerRenderers.getRenderer(answer.getValueType());

            return renderer.renderAnswer(answer);
        } else {
            return new Chunk("No answer provided", FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK));
        }
    }
}
