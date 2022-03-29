package ie.ul.ethics.scieng.exporter.pdf;

import com.itextpdf.text.Element;
/**
 * This interface represents an object that can render
 * a template component
 */
public interface ComponentRenderer {
    /**
     * Render the component into a PDF element
     * @return the rendered element
     */
    Element renderToElement();
}
