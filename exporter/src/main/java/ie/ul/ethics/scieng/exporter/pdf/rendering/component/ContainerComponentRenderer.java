package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.ContainerComponent;

import java.util.Map;

/**
 * Renders a container to PDF
 */
public class ContainerComponentRenderer extends DefaultComponentRenderer {
    /**
     * Instantiate the renderer
     *
     * @param application the application to render
     * @param component   the component being rendered
     */
    public ContainerComponentRenderer(Application application, ApplicationComponent component) {
        super(application, component);
    }

    /**
     * Render the component into a PDF element
     *
     * @param renderOptions a map of key/value render options. The supported options depend on the implementation
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        Chapter chapter = (Chapter) renderOptions.getOrDefault("chapter", new ChapterAutoNumber("")); // a chapter to pass in if one exists to pass to sections

        ContainerComponent containerComponent = (ContainerComponent) component;

        for (ApplicationComponent sub : containerComponent.getComponents()) {
            ComponentType type = sub.getType();

            if (type == ComponentType.SECTION) {
                chapter.add(ComponentRenderers.getRenderer(application, sub).renderToElement(Map.of("chapter", chapter)));
            } else {
                chapter.add(ComponentRenderers.getRenderer(application, sub).renderToElement(renderOptions));
            }
        }

        return chapter;
    }
}
