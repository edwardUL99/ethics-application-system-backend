package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

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
     * Parse the value of the answer. Takes an option "WRAP_VALUE" in the context which if true, the image is aligned center
     * and wrapped in a chunk. If not present, or false, the image is sized according to the document size.
     *
     * @param answer the answer to parse
     * @param context the rendering context
     * @return the element representing the value
     */
    @Override
    protected Element parseAnswerValue(Answer answer, PDFContext context) {
        Path img = writeBase64Image(answer.getValue());
        boolean wrapValue = (Boolean) context.getOption("WRAP_VALUE", false);

        try {
            Image element = Image.getInstance(img.toString());

            if (wrapValue) {
                element.setAlignment(Element.ALIGN_CENTER);

                return new Chunk(element, 0, 0);
            } else {
                Document document = context.getDocument();

                if (document != null) {
                    Rectangle pageSize = document.getPageSize();
                    float documentWidth = pageSize.getWidth() - document.leftMargin() - document.rightMargin();
                    float documentHeight = pageSize.getHeight() - document.topMargin() - document.bottomMargin();
                    element.scaleToFit(documentWidth, documentHeight);
                }

                return element;
            }
        } catch (IOException | BadElementException ex) {
            throw new RuntimeException("Failed to write file", ex);
        }
    }
}
