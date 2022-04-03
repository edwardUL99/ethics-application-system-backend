package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.exporter.pdf.rendering.component.ComponentRenderers;

import java.util.List;
import java.util.Map;

/**
 * This class renders an application template into a PDF element
 */
public class ApplicationTemplateRenderer {
    /**
     * The application being rendered
     */
    private final Application application;

    /**
     * Construct an instance
     * @param application the application being rendered
     */
    public ApplicationTemplateRenderer(Application application) {
        this.application = application;
    }

    /**
     * Render the template's components
     * @param chapter the chapter to add the rendered template to
     * @param template the template to render
     */
    private void renderComponents(Chapter chapter, ApplicationTemplate template) {
        Map<String, Object> renderOptions = Map.of("chapter", chapter);

        for (ApplicationComponent component : template.getComponents()) {
            boolean add = component.getType() != ComponentType.SECTION; // a section adds itself to create the section
            Element rendered = ComponentRenderers.getRenderer(application, component).renderToElement(renderOptions);

            if (add)
                chapter.add(rendered);
        }
    }

    /**
     * Renders the template into an element
     * @return the element to render
     */
    public Element render() {
        ApplicationTemplate template = application.getApplicationTemplate();
        template.sort();

        Paragraph title = new Paragraph();
        title.add(new Chunk(template.getName(), FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK)));
        Chapter chapter = new ChapterAutoNumber(title);

        Paragraph description = new Paragraph();
        description.add(new Chunk(template.getDescription(), FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.LIGHT_GRAY)));
        description.addAll(List.of(Chunk.NEWLINE, Chunk.NEWLINE));

        Phrase versionPhrase = new Phrase();
        versionPhrase.add(new Chunk("Version: ", FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK)));
        versionPhrase.add(new Chunk(template.getVersion(), FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK)));
        description.add(versionPhrase);
        chapter.add(description);
        description.addAll(List.of(Chunk.NEWLINE, Chunk.NEWLINE));

        renderComponents(chapter, template);

        return chapter;
    }
}
