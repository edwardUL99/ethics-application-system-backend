package ie.ul.ethics.scieng.exporter.pdf.rendering.info;

import com.itextpdf.text.Chapter;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.users.models.User;

/**
 * This class renders application information for referred applications
 */
public class ReferredApplicationInfo extends SubmittedApplicationInfo {
    /**
     * A hook to add extra status specific information from the application into the information chapter
     *
     * @param application the application to retrieve information from
     * @param chapter     the chapter the information is being parsed into
     */
    @Override
    protected void addToChapter(Application application, Chapter chapter) {
        super.addToChapter(application, chapter);

        User referrer = application.getReferredBy();

        if (referrer != null)
            addInfoSection(chapter, "Referred By", referrer.getName() + " - " + referrer.getUsername(), NORMAL);
    }
}
