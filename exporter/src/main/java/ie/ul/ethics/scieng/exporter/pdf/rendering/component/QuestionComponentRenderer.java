package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;
import ie.ul.ethics.scieng.exporter.pdf.rendering.AnswerRenderer;
import ie.ul.ethics.scieng.exporter.pdf.rendering.AnswerRenderers;

import java.util.Map;

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
     * The context for rendering
     */
    protected final PDFContext context;

    /**
     * Construct a component instance
     * @param application the application being rendered
     * @param component the component to render
     * @param context rendering context
     */
    public QuestionComponentRenderer(Application application, QuestionComponent component, PDFContext context) {
        this.application = application;
        this.component = component;
        this.context = context;
    }

    /**
     * Render the component into a PDF element
     *
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        Paragraph paragraph = new Paragraph();

        Font font = FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK);

        String titleText = component.getTitle();

        if (titleText != null) {
            Chunk title = new Chunk(titleText, font);
            paragraph.add(title);
            paragraph.add(Chunk.NEWLINE);
        }

        Element description = renderDescription();

        if (description != null) {
            paragraph.add(description);
            paragraph.add(Chunk.NEWLINE);
        }

        Answer answer = application.getAnswers().get(component.getComponentId());

        paragraph.add(renderAnswer(answer));
        paragraph.add(Chunk.NEWLINE);

        Element comments = CommentsRenderer.renderComments(application, component);

        if (comments != null)
            paragraph.add(comments);

        return paragraph;
    }

    /**
     * Render the component description
     * @return the element representing the description, null if no description exists
     */
    protected Element renderDescription() {
        String descriptionValue = component.getDescription();

        if (descriptionValue != null && !descriptionValue.isEmpty()) {
            Font font = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.LIGHT_GRAY);

            return new Chunk(descriptionValue, font);
        }

        return null;
    }

    /**
     * Render the answer of the component
     * @param answer the answer to render
     * @return the rendered answer
     */
    protected Element renderAnswer(Answer answer) {
        if (answer != null) {
            AnswerRenderer renderer = customAnswerRenderer(answer);

            if (renderer == null)
                renderer = AnswerRenderers.getRenderer(answer.getValueType());

            return renderer.renderAnswer(answer, context);
        } else {
            return new Chunk("No answer provided", FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK));
        }
    }

    /**
     * Determines if elements returned from {@link #renderToElement(Map)} should be added or if they are automatically added
     *
     * @return true to add, false to not add
     */
    @Override
    public boolean addReturnedElements() {
        return true;
    }

    /**
     * A hook to define a custom answer render for the renderer. Return null to not define a custom one
     * @param answer the answer being rendered
     * @return the custom renderer, null if not
     */
    protected AnswerRenderer customAnswerRenderer(Answer answer) {
        return null;
    }
}
