package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.Element;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import ie.ul.ethics.scieng.applications.models.applications.Answer;

/**
 * This implementation provides the renderer for rendering options
 */
public class OptionsAnswerRenderer implements AnswerRenderer {
    /**
     * Renders the given answer to a PDF element
     *
     * @param answer the answer to render
     * @return the rendered element
     */
    @Override
    public Element renderAnswer(Answer answer) {
        Answer.ValueType valueType = answer.getValueType();

        if (valueType != Answer.ValueType.OPTIONS) {
            throw new IllegalArgumentException("Invalid answer type " + valueType);
        } else {
            List list = new List(false, 10);
            list.setListSymbol("•");
            String[] split = answer.getValue().split(",");

            for (String value : split) {
                String answerValue = value.contains("=") ? value.split("=")[1]:value;
                list.add(new ListItem(answerValue));
            }

            return list;
        }
    }
}
