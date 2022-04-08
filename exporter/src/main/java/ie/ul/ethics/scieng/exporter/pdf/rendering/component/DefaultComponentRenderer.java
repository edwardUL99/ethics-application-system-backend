package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;

import java.util.Map;

/**
 * This renderer renders the elements common to the ApplicationComponent base class
 */
public class DefaultComponentRenderer implements ComponentRenderer {
    /**
     * The application being rendered
     */
    protected final Application application;
    /**
     * The application component being rendered
     */
    protected final ApplicationComponent component;

    /**
     * Instantiate the renderer
     * @param application the application to render
     * @param component the component being rendered
     */
    public DefaultComponentRenderer(Application application, ApplicationComponent component) {
        this.application = application;
        this.component = component;
    }

    /**
     * Render the component into a PDF element
     *
     * @param renderOptions a map of key/value render options. The supported options depend on the implementation
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        return renderTitle();
    }

    /**
     * Render the component tile
     * @return the title of the component
     */
    protected Element renderTitle() {
        return new Chunk(component.getTitle(), FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK));
    }
}
