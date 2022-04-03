package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPHeaderCell;
import com.itextpdf.text.pdf.PdfPTable;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.applications.templates.components.QuestionTableComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A renderer for rendering question tables
 */
public class QuestionTableComponentRenderer extends DefaultComponentRenderer {
    /**
     * Construct a component instance
     *
     * @param application the application being rendered
     * @param component   the component to render
     */
    public QuestionTableComponentRenderer(Application application, ApplicationComponent component) {
        super(application, component);
    }

    /**
     * Render the component into a PDF element
     *
     * @param renderOptions the options to render
     * @return the rendered element
     */
    @Override
    public Element renderToElement(Map<String, Object> renderOptions) {
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

        Collection<String> columnNames = columns.keySet();
        columnNames.forEach(name -> {
            PdfPHeaderCell header = new PdfPHeaderCell();
            header.setName(name);
            table.addCell(header);
        });

        for (int i = 0; i < tableComponent.getNumRows(); i++) {
            for (String name : columnNames) {
                QuestionTableComponent.Cells cells = columns.get(name);
                Element element =
                        ComponentRenderers.getRenderer(application, cells.getComponents().get(i)).renderToElement(new HashMap<>());
                PdfPCell cell = new PdfPCell();
                cell.addElement(element);
                table.addCell(cell);
            }
        }

        paragraph.add(table);

        return paragraph;
    }
}
