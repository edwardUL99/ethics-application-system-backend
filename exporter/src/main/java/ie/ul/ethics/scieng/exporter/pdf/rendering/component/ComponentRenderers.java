package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.ComponentType;
import ie.ul.ethics.scieng.applications.templates.components.QuestionComponent;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a class that holds component renderers
 */
public final class ComponentRenderers {
    /**
     * Renderers to implement specifically for the component type, else defaults will be used
     */
    private static final Map<ComponentType, Class<? extends ComponentRenderer>> specificRenderers = new HashMap<>();

    static {
        specificRenderers.put(ComponentType.SECTION, SectionComponentRenderer.class);
        specificRenderers.put(ComponentType.CONTAINER, ContainerComponentRenderer.class);
        specificRenderers.put(ComponentType.TEXT, TextComponentRenderer.class);
        specificRenderers.put(ComponentType.QUESTION_TABLE, QuestionTableComponentRenderer.class);
        specificRenderers.put(ComponentType.MULTIPART_QUESTION, MultipartQuestionRenderer.class);
    }

    /**
     * Instantiate the renderer class
     * @param cls the class to instantiate with
     * @param application the application to pass as an argument
     * @param component the component to render
     * @param componentClass the component class
     * @return the instantiated renderer
     */
    private static ComponentRenderer instantiate(Class<? extends ComponentRenderer> cls, Application application, ApplicationComponent component,
                                                 Class<? extends ApplicationComponent> componentClass) {
        if (cls != null) {
            try {
                Constructor<? extends ComponentRenderer> constructor =
                        cls.getDeclaredConstructor(Application.class, (componentClass == null) ? ApplicationComponent.class:componentClass);

                return constructor.newInstance(application, component);
            } catch (NoSuchMethodException ex) {
                if (componentClass != null)
                    throw new IllegalStateException("An implementation of ComponentRenderer must have a constructor that takes Application and ApplicationComponent", ex);
                else
                    return instantiate(cls, application, component, QuestionComponent.class); // try with question component
            } catch(ReflectiveOperationException ex) {
                throw new IllegalStateException("Failed to instantiate renderer", ex);
            }
        } else {
            return null;
        }
    }

    /**
     * Instantiate the renderer class
     * @param cls the class to instantiate with
     * @param application the application to pass as an argument
     * @param component the component to render
     * @return the instantiated renderer
     */
    private static ComponentRenderer instantiate(Class<? extends ComponentRenderer> cls, Application application, ApplicationComponent component) {
        if (cls != null) {
            return instantiate(cls, application, component, null);
        } else {
            return null;
        }
    }

    /**
     * Get the renderer for the given component
     * @param application the application that initiated the rendering
     * @param component the component being rendered
     * @return the renderer for the component type
     */
    public static ComponentRenderer getRenderer(Application application, ApplicationComponent component) {
        return getRenderer(application, component, true);
    }

    /**
     * Get the renderer for the given component
     * @param application the application that initiated the rendering
     * @param component the component being rendered
     * @param returnDefault if true, an instance of ComponentRendererFactory is instantiated as a default, if false, it only returns a specific renderer, else null
     * @return the renderer for the component type
     */
    public static ComponentRenderer getRenderer(Application application, ApplicationComponent component, boolean returnDefault) {
        if (returnDefault) {
            return instantiate(specificRenderers.getOrDefault(component.getType(), ComponentRendererFactory.class), application, component);
        } else {
            return instantiate(specificRenderers.get(component.getType()), application, component);
        }
    }
}
