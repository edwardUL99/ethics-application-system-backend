package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;

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
     * Create an instance
     * @param application the application being rendered
     * @param component the component being rendered
     */
    public ComponentRendererFactory(Application application, ApplicationComponent component) {
        this.application = application;
        this.component = component;
        initialiseDelegate();
    }

    /**
     * Initialise the delegate
     */
    private void initialiseDelegate() {
        ComponentRenderer renderer = ComponentRenderers.getRenderer(application, component, false);

        if (renderer != null) {
            delegate = renderer;
        } else {
            if (component instanceof QuestionComponent) {
                delegate = new QuestionComponentRenderer(application, (QuestionComponent) component);
            } else {
                delegate = new DefaultComponentRenderer(application, component);
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
}
