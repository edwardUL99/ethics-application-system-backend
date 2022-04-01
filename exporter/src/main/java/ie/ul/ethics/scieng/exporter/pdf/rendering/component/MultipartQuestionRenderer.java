package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.MultipartQuestionComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * This renderer renders Multipart questions.
 */
public class MultipartQuestionRenderer extends QuestionComponentRenderer {
    /**
     * Construct a component instance
     *
     * @param application the application being rendered
     * @param component   the component to render
     */
    public MultipartQuestionRenderer(Application application, QuestionComponent component) {
        super(application, component);
    }

    /**
     * Render the component into a PDF element
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        Paragraph paragraph = new Paragraph();
        Chunk title = new Chunk(component.getTitle(), FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK));
        paragraph.add(title);

        MultipartQuestionComponent multipartQuestionComponent = (MultipartQuestionComponent) component;

        Element description = renderDescription();

        if (description != null)
            paragraph.add(description);

        for (Map.Entry<String, MultipartQuestionComponent.QuestionPart> e : multipartQuestionComponent.getParts().entrySet()) {
            String partNumber = e.getKey();
            MultipartQuestionComponent.QuestionPart part = e.getValue();
            Chunk partTitle = new Chunk(partNumber, FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.LIGHT_GRAY));
            paragraph.add(partTitle);
            paragraph.add(ComponentRenderers.getRenderer(application, part.getQuestion()).renderToElement(new HashMap<>()));
        }

        return paragraph;
    }
}
