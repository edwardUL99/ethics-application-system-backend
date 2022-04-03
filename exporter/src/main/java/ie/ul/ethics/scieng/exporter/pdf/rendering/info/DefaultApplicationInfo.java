package ie.ul.ethics.scieng.exporter.pdf.rendering.info;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.ChapterAutoNumber;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
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
     * Parses the default information into the chapter
     * @param application the application to render information for
     * @return the rendered chapter
     */
    private Chapter parseDefaultChapter(Application application) {
        Paragraph title = new Paragraph();
        title.add(new Chunk(application.getApplicationId(), FontFactory.getFont(FontFactory.COURIER_BOLD, 20, BaseColor.BLACK)));
        title.add(Chunk.NEWLINE);
        Chapter chapter = new ChapterAutoNumber(title);
        User applicant = application.getUser();
        LocalDateTime updated = application.getLastUpdated();
        String status = application.getStatus().label();

        Phrase statusPhrase = new Phrase();
        statusPhrase.add(new Chunk("Status: " , BOLD));
        statusPhrase.add(new Chunk(status, NORMAL));

        Phrase applicantPhrase = new Phrase();
        applicantPhrase.add(new Chunk("Applicant: ", BOLD));
        applicantPhrase.add(new Chunk(String.format("%s - %s", applicant.getName(), applicant.getUsername()), NORMAL));

        Phrase updatedPhrase = new Phrase();
        updatedPhrase.add(new Chunk("Last Updated: ", BOLD));
        updatedPhrase.add(new Chunk((updated == null) ? "N/A":updated.format(DATE_FORMAT), NORMAL));

        chapter.add(statusPhrase);
        chapter.add(Chunk.NEWLINE);
        chapter.add(applicantPhrase);
        chapter.add(Chunk.NEWLINE);
        chapter.add(updatedPhrase);
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
