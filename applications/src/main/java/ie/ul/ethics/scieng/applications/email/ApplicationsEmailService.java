package ie.ul.ethics.scieng.applications.email;

import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.common.email.AsyncEmailService;
import ie.ul.ethics.scieng.common.email.EmailSender;
import ie.ul.ethics.scieng.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This service provides the email notifications that are sent from the applications module
 */
@Service("applicationsEmail")
public class ApplicationsEmailService extends AsyncEmailService {
    /**
     * Instantiate the email service
     *
     * @param sender the object to use for sending emails
     */
    @Autowired
    protected ApplicationsEmailService(EmailSender sender) {
        super(sender);
    }

    /**
     * Send the email notifying the creator of the provided application that their application has been referred back
     * to them by the specified referrer
     * @param application the application being referred
     * @param referrer the user referring the application
     */
    public void sendApplicationReferredEmail(Application application, User referrer) {
        String content = "<h2>Application Referred - %s</h2>"
                + "<p>Hello %s,<br>This e-mail is a quick notification that your ethics application"
                + " has been referred back to you for more information. This means that some fields of your application"
                + "requires some attention from you before we can proceed with reviewing your application</p>"
                + "<br>"
                + "<p>Application ID: <b>%s</b></p>"
                + "<p>Your application has been referred by: %s (%s)</p>"
                + "<br>"
                + "<h4>What do I need to do?</h4>"
                + "<p>You can go to your application to review the actions required of you by clicking: <a href=\"%s\">%s</a></p>"
                + "<br>"
                + "If for some reason, the link doesn't work, paste the following link into your browser: %s</p>"
                + "<br>"
                + "<p>Once you have reviewed the changes required from you, you can re-submit the application to the committee</p>"
                + "<br>"
                + "<p>Thank You,</p>"
                + "<p>The Team</p>";

        User applicant = application.getUser();
        String applicationId = application.getApplicationId();

        String urlBase = getFrontendURL();
        urlBase = urlBase + "/applications?id=" + applicationId;

        String email = applicant.getAccount().getEmail();
        content = String.format(content, applicationId, applicant.getName(), applicationId,
                referrer.getName(), referrer.getUsername(), urlBase, applicationId, urlBase);

        sendEmail(email, "Application " + applicationId + " referred for more information", content);
    }
}
