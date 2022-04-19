package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.ContainerComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

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
     * @param context     for rendering
     */
    public ContainerComponentRenderer(Application application, ApplicationComponent component, PDFContext context) {
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
        if (renderOptions == null) {
            throw new IllegalArgumentException("The renderOptions map is expected by the SectionComponentRenderer");
        } else {
            Chapter chapter = (Chapter) renderOptions.get("chapter");

            if (chapter == null) {
                throw new IllegalArgumentException("You must provide a Chapter object in the renderOptions map");
            } else {
                ContainerComponent containerComponent = (ContainerComponent) component;

                for (ApplicationComponent sub : containerComponent.getComponents()) {
                    ComponentType type = sub.getType();
                    ComponentRenderer renderer = ComponentRenderers.getRenderer(application, context, sub);
                    boolean add = renderer.addReturnedElements();

                    Element element;
                    if (type == ComponentType.SECTION | type == ComponentType.CONTAINER) {
                        element = renderer.renderToElement(Map.of("chapter", chapter));
                    } else {
                        element = renderer.renderToElement(renderOptions);
                    }

                    if (add)
                        chapter.add(element);
                }

                return chapter;
            }
        }
    }

    /**
     * Determines if elements returned from {@link #renderToElement(Map)} should be added or if they are automatically added
     *
     * @return true to add, false to not add
     */
    @Override
    public boolean addReturnedElements() {
        return false;
    }
}
