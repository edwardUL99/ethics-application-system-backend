package ie.ul.ethics.scieng.exporter.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import ie.ul.ethics.scieng.applications.models.applications.Answer;

/**
 * Renders a text/number value answer
 */
public class TextNumberAnswerRenderer implements AnswerRenderer {
    /**
     * Renders the given answer to a PDF element
     *
     * @param answer the answer to render
     * @return the rendered element
     */
    @Override
    public Element renderAnswer(Answer answer) {
        Answer.ValueType valueType = answer.getValueType();

        if (valueType != Answer.ValueType.TEXT && valueType != Answer.ValueType.NUMBER) {
            throw new IllegalArgumentException("Invalid answer type " + valueType);
        } else {
            return new Chunk(answer.getValue(), FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK));
        }
    }
}
