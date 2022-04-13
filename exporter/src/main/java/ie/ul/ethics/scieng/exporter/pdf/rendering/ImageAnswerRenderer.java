package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import ie.ul.ethics.scieng.applications.models.applications.Answer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/**
 * This renderer renders Image answers
 */
public class ImageAnswerRenderer extends BaseAnswerRenderer {
    /**
     * Write the base 64 image to a temp image file
     * @param value the value to write
     * @return the path to the temp file
     */
    private Path writeBase64Image(String value) {
        try {
            Path temp = Files.createTempFile("temp-image", ".png");
            value = value.substring(value.indexOf(",") + 1);

            byte[] decoded = Base64.getDecoder().decode(value.getBytes(StandardCharsets.UTF_8));
            Files.write(temp, decoded);

            return temp;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to write file", ex);
        }
    }

    /**
     * Parse the value of the answer
     *
     * @param answer the answer to parse
     * @return the element representing the value
     */
    @Override
    protected Element parseAnswerValue(Answer answer) {
        Path img = writeBase64Image(answer.getValue());
        try {
            return Image.getInstance(img.toString());
        } catch (IOException | BadElementException ex) {
            throw new RuntimeException("Failed to write file", ex);
        }
    }
}
