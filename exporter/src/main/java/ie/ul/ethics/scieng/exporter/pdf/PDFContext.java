package ie.ul.ethics.scieng.exporter.pdf;

import com.itextpdf.text.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a context for storing the current document
 */
public final class PDFContext {
    /**
     * The current document in the context
     */
    private Document document;
    /**
     * Options to set in the context
     */
    private final Map<String, Object> options = new HashMap<>();

    /**
     * Set the document in the context
     * @param document the document to set
     */
    public void setDocument(Document document) {
        this.document = document;
    }

    /**
     * Retrieve the current document
     * @return the current document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Set an option on the context
     * @param name the name of the option
     * @param option the option to set
     */
    public void setOption(String name, Object option) {
        this.options.put(name, option);
    }

    /**
     * Get an option with a default value
     * @param name the name of the option
     * @param defaultOption the default option to return if no option with name exists
     * @return the found option
     */
    public Object getOption(String name, Object defaultOption) {
        return this.options.getOrDefault(name, defaultOption);
    }

    /**
     * Remove the option with the given name from the context
     * @param name the name to remove
     */
    public void removeOption(String name) {
        this.options.remove(name);
    }
}
