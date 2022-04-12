package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.Element;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import ie.ul.ethics.scieng.applications.models.applications.Answer;

/**
 * This implementation provides the renderer for rendering options
 */
public class OptionsAnswerRenderer extends BaseAnswerRenderer {
    /**
     * Parse the value of the answer
     *
     * @param answer the answer to parse
     * @return the element representing the value
     */
    @Override
    protected Element parseAnswerValue(Answer answer) {
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
