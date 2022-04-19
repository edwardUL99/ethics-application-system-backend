package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

import java.util.Map;

/**
 * This is an implementation of the ComponentRenderer interface but acts as a factory, delegating to a specific implementation
 * based on the given component
 */
public class ComponentRendererFactory implements ComponentRenderer {
    /**
     * The application being rendered
     */
    private final Application application;
    /**
     * The component being rendered
     */
    private final ApplicationComponent component;
    /**
     * The renderer being delegated to
     */
    private ComponentRenderer delegate;
    /**
     * Rendering context
     */
    private PDFContext context;

    /**
     * Create an instance
     * @param application the application being rendered
     * @param component the component being rendered
     * @param context rendering context
     */
    public ComponentRendererFactory(Application application, ApplicationComponent component, PDFContext context) {
        this.application = application;
        this.component = component;
        this.context = context;
        initialiseDelegate();
    }

    /**
     * Initialise the delegate
     */
    private void initialiseDelegate() {
        ComponentRenderer renderer = ComponentRenderers.getRenderer(application, context, component, false);

        if (renderer != null) {
            delegate = renderer;
        } else {
            if (component instanceof QuestionComponent) {
                delegate = new QuestionComponentRenderer(application, (QuestionComponent) component, context);
            } else {
                delegate = new DefaultComponentRenderer(application, component, context);
            }
        }
    }

    /**
     * Render the component into a PDF element
     *
     * @param renderOptions a map of key/value render options. The supported options depend on the implementation
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        return (delegate != null) ? delegate.renderToElement(renderOptions):null;
    }

    /**
     * Determines if elements returned from {@link #renderToElement(Map)} should be added or if they are automatically added
     *
     * @return true to add, false to not add
     */
    @Override
    public boolean addReturnedElements() {
        return delegate != null && delegate.addReturnedElements();
    }
}
