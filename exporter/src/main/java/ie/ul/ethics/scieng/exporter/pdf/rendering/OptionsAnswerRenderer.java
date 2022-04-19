package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.Element;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

/**
 * This implementation provides the renderer for rendering options
 */
public class OptionsAnswerRenderer extends BaseAnswerRenderer {
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

        if (valueType != Answer.ValueType.OPTIONS) {
            throw new IllegalArgumentException("Invalid answer type " + valueType);
        } else {
            List list = new List(false, 10);
            list.setListSymbol("•");
            String[] split = answer.getValue().split(",");

            for (String value : split)
                list.add(new ListItem(parseOptionValue(value)));

            return list;
        }
    }

    /**
     * Parses the value in the options answer to a value to display in the answer
     * @param value the value to render
     * @return the answer value
     */
    protected String parseOptionValue(String value) {
        return value.contains("=") ? value.split("=")[1]:value;
    }
}
