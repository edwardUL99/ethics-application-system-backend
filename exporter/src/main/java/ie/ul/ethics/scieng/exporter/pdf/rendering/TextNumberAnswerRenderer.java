package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

/**
 * Renders a text/number value answer
 */
public class TextNumberAnswerRenderer extends BaseAnswerRenderer {
    /**
     * Parse the value of the answer
     *
     * @param answer the answer to parse
     * @param context the rendering context
     * @return the element representing the value
     */
    @Override
    protected Element parseAnswerValue(Answer answer, PDFContext context) {
        Answer.ValueType valueType = answer.getValueType();

        if (valueType != Answer.ValueType.TEXT && valueType != Answer.ValueType.NUMBER) {
            throw new IllegalArgumentException("Invalid answer type " + valueType);
        } else {
            return new Chunk(answer.getValue(), FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK));
        }
    }
}
