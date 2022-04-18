package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;
import ie.ul.ethics.scieng.users.models.User;

/**
 * This provides the base answerer renderer
 */
public abstract class BaseAnswerRenderer implements AnswerRenderer {
    /**
     * Renders the given answer to a PDF element
     *
     * @param answer the answer to render
     * @param context the rendering context
     * @return the rendered element
     */
    @Override
    public Element renderAnswer(Answer answer, PDFContext context) {
        Paragraph paragraph = new Paragraph();
        User user = answer.getUser();
        Element value = parseAnswerValue(answer, context);

        if (user == null) {
            return value;
        } else {
            paragraph.add(value);
            paragraph.add(Chunk.NEWLINE);

            Phrase phrase = new Phrase();
            phrase.add(new Chunk("Answered By: ", FontFactory.getFont(FontFactory.COURIER_BOLD, 14)));
            phrase.add(new Chunk(user.getName() + " - " + user.getUsername(), FontFactory.getFont(FontFactory.COURIER, 14)));
            paragraph.add(phrase);

            return paragraph;
        }
    }

    /**
     * Parse the value of the answer
     * @param answer the answer to parse
     * @param context the rendering context
     * @return the element representing the value
     */
    protected abstract Element parseAnswerValue(Answer answer, PDFContext context);
}
