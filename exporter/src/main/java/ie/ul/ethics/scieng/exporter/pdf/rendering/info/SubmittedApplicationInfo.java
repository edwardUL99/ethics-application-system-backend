package ie.ul.ethics.scieng.exporter.pdf.rendering.info;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.AssignedCommitteeMember;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.users.models.User;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * This class renders application information for submitted applications
 */
public class SubmittedApplicationInfo extends DefaultApplicationInfo {
    /**
     * Render a final comment to the chapter
     * @param finalComment the comment to render
     * @param chapter the chapter to add the comment to
     */
    private void renderFinalComment(Comment finalComment, Chapter chapter) {
        if (finalComment != null) {
            Section section = chapter.addSection(createTitle("Final Comment", 18));
            Paragraph paragraph = new Paragraph();
            section.add(paragraph);

            User user = finalComment.getUser();
            paragraph.add(new Chunk(user.getName() + " - " + user.getUsername(), BOLD));
            paragraph.add(Chunk.NEWLINE);
            paragraph.add(new Chunk(finalComment.getCreatedAt().format(DATE_FORMAT), NORMAL));
            paragraph.add(Chunk.NEWLINE);
            paragraph.add(new Chunk(finalComment.getComment(), NORMAL));
            paragraph.add(Chunk.NEWLINE);
            paragraph.add(Chunk.NEWLINE);
        }
    }

    /**
     * Render the list of assigned committee members
     * @param chapter the chapter to add the section to
     * @param assigned the list of assigned committee members
     */
    private void renderAssignedCommitteeMembers(Chapter chapter, Collection<AssignedCommitteeMember> assigned) {
        if (assigned.size() > 0) {
            Paragraph title = createTitle("Assigned Committee Members", 18);
            Section section = chapter.addSection(title);

            List assignedList = new List();
            assignedList.setListSymbol("•");

            for (AssignedCommitteeMember member : assigned) {
                User user = member.getUser();
                Chunk chunk = new Chunk(String.format("%s - %s - %s", user.getName(), user.getUsername(), (member.isFinishReview()) ? "Finished Review" : "Reviewing"),
                        FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK));
                assignedList.add(new ListItem(chunk));
            }

            section.add(assignedList);
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
                addInfoSection(chapter, "Submitted At", submitted.format(DATE_FORMAT), NORMAL);
                chapter.add(Chunk.NEWLINE);
            }
        } else {
            LocalDateTime approvedTime = application.getApprovalTime();

            if (approvedTime != null) {
                addInfoSection(chapter, "Approved At", approvedTime.format(DATE_FORMAT), NORMAL);
                chapter.add(Chunk.NEWLINE);
            }

            renderFinalComment(application.getFinalComment(), chapter);
        }

        renderAssignedCommitteeMembers(chapter, application.getAssignedCommitteeMembers());
    }
}
