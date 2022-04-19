package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

/**
 * Renders an application answer into a PDF element
 */
public interface AnswerRenderer {
    /**
     * Renders the given answer to a PDF element
     * @param answer the answer to render
     * @param context the context for rendering
     * @return the rendered element
     */
    Element renderAnswer(Answer answer, PDFContext context);
}
