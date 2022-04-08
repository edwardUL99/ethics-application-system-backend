package ie.ul.ethics.scieng.exporter.pdf.rendering.component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationComments;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.templates.components.ApplicationComponent;
import ie.ul.ethics.scieng.users.models.User;

import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * A class that renders application comments
 */
public final class CommentsRenderer {
    /**
     * Render the provided comment
     * @param comment the comment to render
     * @param indentation the indentation to set
     * @return the rendered comment
     */
    private static Element renderComment(Comment comment, int indentation) {
        Paragraph paragraph = new Paragraph();
        paragraph.setIndentationLeft(indentation);
        User user = comment.getUser();
        String created = comment.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        if (user != null) {
            paragraph.add(new Chunk(String.format("%s - %s", user.getName(), user.getUsername()),
                    FontFactory.getFont(FontFactory.COURIER_BOLD, 14, BaseColor.BLACK)));
            paragraph.add(Chunk.NEWLINE);
        }

        paragraph.add(new Chunk(created, FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.GRAY)));
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(Chunk.NEWLINE);
        paragraph.add(new Chunk(comment.getComment(), FontFactory.getFont(FontFactory.COURIER, 11, BaseColor.BLACK)));

        for (Comment sub : comment.getSubComments()) {
            paragraph.add(renderComment(sub, indentation + 25));
            paragraph.add(Chunk.NEWLINE);
            paragraph.add(Chunk.NEWLINE);
        }

        return paragraph;
    }

    /**
     * Renders the comments into the paragraph
     * @param paragraph the paragraph to render the comments into
     * @param comments the comments to render
     */
    private static void renderComments(Paragraph paragraph, ApplicationComments comments) {
        for (Comment comment : comments.getComments())
            paragraph.add(renderComment(comment, 0));
    }

    /**
     * Render the comments for the component
     * @param application the application being rendered
     * @param component the component the comment is being added to
     * @return the rendered element or null if no comments present
     */
    public static Element renderComments(Application application, ApplicationComponent component) {
        Map<String, ApplicationComments> applicationComments = application.getComments();
        ApplicationComments comments;

        if (applicationComments != null && (comments = applicationComments.get(component.getComponentId())) != null) {
            Paragraph paragraph = new Paragraph();
            paragraph.add(Chunk.NEWLINE);
            paragraph.add(new Chunk("Comments", FontFactory.getFont(FontFactory.COURIER_BOLD, 15, BaseColor.BLACK)));
            paragraph.add(Chunk.NEWLINE);

            renderComments(paragraph, comments);

            return paragraph;
        } else {
            return null;
        }
    }
}
