package ie.ul.ethics.scieng.exporter.pdf;

import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Answer;

/**
 * Renders an application answer into a PDF element
 */
public interface AnswerRenderer {
    /**
     * Renders the given answer to a PDF element
     * @param answer the answer to render
     * @return the rendered element
     */
    Element renderAnswer(Answer answer);
}
