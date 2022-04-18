package ie.ul.ethics.scieng.exporter.pdf.rendering;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.exporter.exceptions.ExportException;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;
import ie.ul.ethics.scieng.exporter.pdf.rendering.info.InformationRenderers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Renders an application to a PDF
 */
public class ApplicationRenderer {
    /**
     * The application to render
     */
    private final Application application;

    /**
     * Create an instance
     * @param application the application to render
     */
    public ApplicationRenderer(Application application) {
        this.application = application;
    }

    /**
     * Gets the application info element
     * @return the information element
     */
    private Element getApplicationInfo() {
        return InformationRenderers.getApplicationInfo(application.getStatus()).renderInfo(application);
    }

    /**
     * Parse the template into PDF
     * @param context the context for rendering
     * @return the parsed template
     */
    private Element parseTemplate(PDFContext context) {
        return new ApplicationTemplateRenderer(application, context).render();
    }

    /**
     * Render the application to a PDF document
     * @return the stream containing the exported PDF
     */
    public InputStream render() {
        try {
            PDFContext context = new PDFContext();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, outputStream);

            context.setDocument(document);

            document.open();
            document.add(getApplicationInfo());
            document.add(parseTemplate(context));
            document.close();

            context.setDocument(null);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (DocumentException ex) {
            throw new ExportException("Failed to export to PDF", ex);
        }
    }
}
