package ie.ul.ethics.scieng.exporter.pdf.rendering.info;

import com.itextpdf.text.*;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.users.models.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A default information implementation to render common information. To add supplementary information,
 * override addToChapter
 */
public class DefaultApplicationInfo implements ApplicationInfo {
    /**
     * Bold font for info labels
     */
    protected final Font BOLD = FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK);
    /**
     * Normal font for info values
     */
    protected final Font NORMAL = FontFactory.getFont(FontFactory.COURIER, 14, BaseColor.BLACK);
    /**
     * The formatter for date values
     */
    protected final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Render the info into a PDF element
     *
     * @param application the application to render the info of
     * @return the element representing the rendered info
     */
    @Override
    public Element renderInfo(Application application) {
        Chapter defaultChapter = parseDefaultChapter(application);
        addToChapter(application, defaultChapter);

        return defaultChapter;
    }

    /**
     * Parse the title of the section
     * @param text the title text
     * @param size font size
     * @return the element representing the title
     */
    protected Paragraph createTitle(String text, int size) {
        Paragraph title = new Paragraph();
        title.add(new Chunk(text, FontFactory.getFont(FontFactory.COURIER_BOLD, size, BaseColor.BLACK)));
        title.add(Chunk.NEWLINE);

        return title;
    }

    /**
     * Parse the title and content into an info section
     * @param chapter the chapter containing the application info
     * @param title info title
     * @param content the content to display in the section
     * @param font the content font
     */
    protected void addInfoSection(Chapter chapter, String title, String content, Font font) {
        Section section = chapter.addSection(createTitle(title, 18));
        section.add(new Chunk(content, font));
    }

    /**
     * Parses the default information into the chapter
     * @param application the application to render information for
     * @return the rendered chapter
     */
    private Chapter parseDefaultChapter(Application application) {
        Chapter chapter = new ChapterAutoNumber(createTitle("Application " + application.getApplicationId(), 20));
        chapter.add(Chunk.NEWLINE);

        User applicant = application.getUser();
        LocalDateTime updated = application.getLastUpdated();
        String status = application.getStatus().label();

        addInfoSection(chapter, "Application Status", status, NORMAL);
        chapter.add(Chunk.NEWLINE);
        addInfoSection(chapter, "Applicant", String.format("%s - %s", applicant.getName(), applicant.getUsername()), NORMAL);
        chapter.add(Chunk.NEWLINE);
        addInfoSection(chapter, "Last Updated", (updated == null) ? "N/A":updated.format(DATE_FORMAT), NORMAL);
        chapter.add(Chunk.NEWLINE);

        return chapter;
    }

    /**
     * A hook to add extra status specific information from the application into the information chapter
     * @param application the application to retrieve information from
     * @param chapter the chapter the information is being parsed into
     */
    protected void addToChapter(Application application, Chapter chapter) {
        // default no-op
    }
}
