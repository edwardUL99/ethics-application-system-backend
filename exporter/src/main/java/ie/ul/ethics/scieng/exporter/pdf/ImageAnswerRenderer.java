package ie.ul.ethics.scieng.exporter.pdf;

import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Answer;

/**
 * This renderer renders Image answers
 */
public class ImageAnswerRenderer implements AnswerRenderer {
    /**
     * Renders the given answer to a PDF element
     *
     * @param answer the answer to render
     * @return the rendered element
     */
    @Override
    public Element renderAnswer(Answer answer) {
        return null;
    }
}
