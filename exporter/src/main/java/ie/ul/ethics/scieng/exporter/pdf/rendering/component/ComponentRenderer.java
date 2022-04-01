package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.Element;

import java.util.Map;

/**
 * This interface represents an object that can render
 * a template component
 */
public interface ComponentRenderer {
    /**
     * Render the component into a PDF element
     * @param renderOptions a map of key/value render options. The supported options depend on the implementation
     * @return the rendered element
     */
    Element renderToElement(Map<String, Object> renderOptions);
}
