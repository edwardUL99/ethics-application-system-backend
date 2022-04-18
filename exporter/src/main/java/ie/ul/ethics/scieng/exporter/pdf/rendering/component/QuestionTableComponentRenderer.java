package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionTableComponent;
import ie.ul.ethics.scieng.exporter.pdf.PDFContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A renderer for rendering question tables
 */
public class QuestionTableComponentRenderer extends DefaultComponentRenderer {
    /**
     * Instantiate the renderer
     *
     * @param application the application to render
     * @param component   the component being rendered
     * @param context     for rendering
     */
    public QuestionTableComponentRenderer(Application application, ApplicationComponent component, PDFContext context) {
        super(application, component, context);
    }

    /**
     * Create a table cell
     * @param element the element to add to the cell
     * @param align true to align the element in the cell
     * @return the table cell
     */
    private PdfPCell createCell(Element element, boolean align) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(element);

        if (align)
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        return cell;
    }

    /**
     * Render the component into a PDF element
     *
     * @param renderOptions the options to render
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
        context.setOption("WRAP_VALUE", true); // image answer render accepts this option. Any image in the table is wrapped, so set the option to true

        Paragraph paragraph = new Paragraph();
        QuestionTableComponent tableComponent = (QuestionTableComponent) component;

        String title = tableComponent.getTitle();

        if (title != null) {
            paragraph.add(new Chunk(title, FontFactory.getFont(FontFactory.COURIER_BOLD, 16, BaseColor.BLACK)));
            paragraph.add(Chunk.NEWLINE);
        }

        QuestionTableComponent.CellsMapping cellsMapping = tableComponent.getCells();
        Map<String, QuestionTableComponent.Cells> columns = cellsMapping.getColumns();

        PdfPTable table = new PdfPTable(columns.size());
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.setWidthPercentage(100.0f);

        Collection<String> columnNames = columns.keySet();
        columnNames.forEach(name -> table.addCell(createCell(new Chunk(name, FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK)), false)));

        for (int i = 0; i < tableComponent.getNumRows(); i++) {
            for (String name : columnNames) {
                QuestionTableComponent.Cells cells = columns.get(name);
                Element element =
                        ComponentRenderers.getRenderer(application, context, cells.getComponents().get(i)).renderToElement(new HashMap<>());
                table.addCell(createCell(element, true));
            }
        }

        paragraph.add(table);

        context.removeOption("WRAP_VALUE"); // disable wrapping for other image answers

        return paragraph;
    }
}
