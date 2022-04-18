package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

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
     * The context for rendering
     */
    protected final PDFContext context;

    /**
     * Instantiate the renderer
     * @param application the application to render
     * @param component the component being rendered
     * @param context for rendering
     */
    public DefaultComponentRenderer(Application application, ApplicationComponent component, PDFContext context) {
        this.application = application;
        this.component = component;
        this.context = context;
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

    /**
     * Determines if elements returned from {@link #renderToElement(Map)} should be added or if they are automatically added
     *
     * @return true to add, false to not add
     */
    @Override
    public boolean addReturnedElements() {
        return true;
    }
}
