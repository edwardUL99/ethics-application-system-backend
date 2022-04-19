package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.TextComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

import java.util.Map;

/**
 * A simple renderer for rendering a text component
 */
public class TextComponentRenderer extends DefaultComponentRenderer {
    /**
     * Instantiate the renderer
     *
     * @param application the application to render
     * @param component   the component being rendered
     * @param context     for rendering
     */
    public TextComponentRenderer(Application application, ApplicationComponent component, PDFContext context) {
        super(application, component, context);
    }

    /**
     * Render the component into a PDF element
     *
     * @param renderOptions a map of key/value render options. The supported options depend on the implementation
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(renderTitle());
        paragraph.add(Chunk.NEWLINE);

        TextComponent textComponent = (TextComponent) component;
        paragraph.add(new Chunk(textComponent.getContent(), FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.LIGHT_GRAY)));
        paragraph.add(Chunk.NEWLINE);

        return paragraph;
    }
}
