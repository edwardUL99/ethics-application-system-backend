package ie.ul.ethics.scieng.exporter.pdf.rendering;

import ie.ul.ethics.scieng.applications.models.applications.Answer;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to register and retrieve renderers for answers
 */
public final class AnswerRenderers {
    /**
     * Map of registered renderers
     */
    private static final Map<Answer.ValueType, AnswerRenderer> renderers = new HashMap<>();

    static {
        register(Answer.ValueType.TEXT, new TextNumberAnswerRenderer());
        register(Answer.ValueType.NUMBER, new TextNumberAnswerRenderer());
        register(Answer.ValueType.OPTIONS, new OptionsAnswerRenderer());
        register(Answer.ValueType.IMAGE, new ImageAnswerRenderer());
    }

    /**
     * Register the renderer with the given value type
     * @param valueType the type of the answer value to render
     * @param renderer the renderer for that value type
     */
    public static void register(Answer.ValueType valueType, AnswerRenderer renderer) {
        renderers.put(valueType, renderer);
    }

    /**
     * Get the renderer registered for the given value type
     * @param valueType the value type to look-up the renderer with
     * @return the renderer for the given value type
     */
    public static AnswerRenderer getRenderer(Answer.ValueType valueType) {
        AnswerRenderer renderer = renderers.get(valueType);

        if (renderer == null)
            throw new IllegalArgumentException("No renderer registered for value type: " + valueType);

        return renderer;
    }
}
