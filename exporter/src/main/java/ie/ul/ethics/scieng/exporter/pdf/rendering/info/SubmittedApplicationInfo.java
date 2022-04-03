package ie.ul.ethics.scieng.exporter.pdf.rendering.info;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.users.models.User;

import java.time.LocalDateTime;

public class SubmittedApplicationInfo extends DefaultApplicationInfo {
    /**
     * Render a final comment to the chapter
     * @param finalComment the comment to render
     * @param chapter the chapter to add the comment to
     */
    private void renderFinalComment(Comment finalComment, Chapter chapter) {
        if (finalComment != null) {
            Section section = chapter.addSection("Final Comment");
            Paragraph paragraph = new Paragraph();
            section.add(paragraph);

            User user = finalComment.getUser();
            paragraph.add(new Chunk(user.getName() + " - " + user.getUsername(), BOLD));
            paragraph.add(new Chunk(finalComment.getCreatedAt().format(DATE_FORMAT), NORMAL));
            paragraph.add(new Chunk(finalComment.getComment(), NORMAL));
        }
    }

    /**
     * A hook to add extra status specific information from the application into the information chapter
     *
     * @param application the application to retrieve information from
     * @param chapter     the chapter the information is being parsed into
     */
    @Override
    protected void addToChapter(Application application, Chapter chapter) {
        super.addToChapter(application, chapter);
        boolean approved = application.getStatus() == ApplicationStatus.APPROVED;

        if (!approved) {
            LocalDateTime submitted = application.getSubmittedTime();

            if (submitted != null) {
                Phrase submittedPhrase = new Phrase();
                submittedPhrase.add(new Chunk("Submitted At: ", BOLD));
                submittedPhrase.add(new Chunk(submitted.format(DATE_FORMAT), NORMAL));

                chapter.add(submittedPhrase);
                chapter.add(Chunk.NEWLINE);
            }
        } else {
            LocalDateTime approvedTime = application.getApprovalTime();

            if (approvedTime != null) {
                Phrase approvedPhrase = new Phrase();
                approvedPhrase.add(new Chunk("Approved At: ", BOLD));
                approvedPhrase.add(new Chunk(approvedTime.format(DATE_FORMAT), NORMAL));

                chapter.add(approvedPhrase);
                chapter.add(Chunk.NEWLINE);
            }

            renderFinalComment(application.getFinalComment(), chapter);
        }
    }
}
