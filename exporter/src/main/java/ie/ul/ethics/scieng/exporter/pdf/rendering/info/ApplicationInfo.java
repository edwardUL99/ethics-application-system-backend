package ie.ul.ethics.scieng.exporter.pdf.rendering.info;

import com.itextpdf.text.Element;
import ie.ul.ethics.scieng.applications.models.applications.Application;

/**
 * This interface renders the application info based on status
 */
public interface ApplicationInfo {
    /**
     * Render the info into a PDF element
     * @param application the application to render the info of
     * @return the element representing the rendered info
     */
    Element renderInfo(Application application);
}
