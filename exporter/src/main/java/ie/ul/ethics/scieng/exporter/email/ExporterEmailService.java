package ie.ul.ethics.scieng.exporter.email;

import ie.ul.ethics.scieng.common.email.AsyncEmailService;
import ie.ul.ethics.scieng.common.email.EmailSender;
import ie.ul.ethics.scieng.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An e-mail service for the exporter module
 */
@Service("exporterEmail")
public class ExporterEmailService extends AsyncEmailService {
    /**
     * Instantiate the email service
     *
     * @param sender the object to use for sending emails
     */
    @Autowired
    protected ExporterEmailService(EmailSender sender) {
        super(sender);
    }

    /**
     * Send the e-mail notifying the user that the export task completed and give them the download link
     * @param user the user to send the e-mail to
     * @param name the name of the ZIP file to download
     * @param requestedAt the timestamp of when the export task was requested
     */
    public void sendExportLinkEmail(User user, String name, LocalDateTime requestedAt) {
        String content = "<h2>Application Export Success</h2>"
                + "<p>Hello %s<br>This e-mail is a quick notification that the export application "
                + "task you requested at <b>%s</b> has been completed successfully</p>"
                + "<br>"
                + "<h3>Download the Exported File</h3>"
                + "<p>The file is a ZIP archive of the application(s) that you have requested to be exported, with each "
                + "application in PDF format and that application's attachments in another zip called <i>attachments.zip</i><p>"
                + "<p>Follow this link to download the archive: <a href=\"%s\">Download Archive</a></p>"
                + "<p>If for some reason, the link doesn't work, paste the following link into your browser: %s</p>";

        String urlBase = getFrontendURL();
        urlBase = urlBase + "/export-downloader?filename=";
        String filename = URLEncoder.encode(name, StandardCharsets.UTF_8);
        urlBase += filename;

        String userName = user.getName();
        String email = user.getAccount().getEmail();
        String requested = requestedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        content = String.format(content, userName, requested, urlBase, urlBase);

        sendEmail(email, String.format("Application Export Successful - %s", requested), content);
    }

    /**
     * Send the e-mail notifying the user that the export task failed
     * @param user the user to send the e-mail to
     * @param requestedAt the timestamp of when the export task was requested
     */
    public void sendExportFailedEmail(User user, LocalDateTime requestedAt) {
        String content = "<h2>Application Export Failure</h2>"
                + "<p>Hello %s<br>This e-mail is a quick notification that the export application "
                + "task you requested at <b>%s</b> could not be completed successfully</p>"
                + "<br>"
                + "<h3>What do I need to do?</h3>"
                + "<p>You can try exporting the application(s) again. If the error persists, please try again later<p>"
                + "<p>Sorry for any inconvenience caused</p>";

        String userName = user.getName();
        String email = user.getAccount().getEmail();
        String requested = requestedAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        content = String.format(content, userName, requested);

        sendEmail(email, String.format("Application Export Failure - %s", requested), content);
    }
}
