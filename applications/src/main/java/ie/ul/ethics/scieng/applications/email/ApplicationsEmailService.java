package ie.ul.ethics.scieng.applications.email;

import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.answerrequest.AnswerRequest;
import ie.ul.ethics.scieng.common.email.AsyncEmailService;
import ie.ul.ethics.scieng.common.email.EmailSender;
import ie.ul.ethics.scieng.users.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
                + " has been referred back to you for more information. This means that some fields of your application "
                + "requires some attention from you before we can proceed with reviewing your application</p>"
                + "<p>Application ID: <b>%s</b></p>"
                + "<p>Your application has been referred by: %s (%s)</p>"
                + "<h4>What do I need to do?</h4>"
                + "<p>You can go to your application to review the actions required of you by clicking: <a href=\"%s\">%s</a></p>"
                + "<p>If for some reason, the link doesn't work, paste the following link into your browser: %s</p>"
                + "<br>"
                + "<p>Once you have reviewed the changes required from you, you can re-submit the application to the committee</p>";

        User applicant = application.getUser();
        String applicationId = application.getApplicationId();

        String urlBase = getFrontendURL();
        urlBase = urlBase + "/application?id=" + applicationId;

        String email = applicant.getAccount().getEmail();
        content = String.format(content, applicationId, applicant.getName(), applicationId,
                referrer.getName(), referrer.getUsername(), urlBase, applicationId, urlBase);

        sendEmail(email, String.format("Application " + applicationId + " referred for more information - %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))), content);
    }

    /**
     * Parse the final comment string
     * @param finalComment the comment to parse into a HTML comment for the e-mail
     * @return the comment as HTML
     */
    private String parseFinalComment(Comment finalComment) {
        String container = "<h4>Final Comment</h4>"
                + "<div style=\"border: 1px solid grey; padding: 2px;\">"
                + "%s"
                + "</div>";

        String content;

        if (finalComment == null) {
            content = "No comment provided";
        } else {
            User user = finalComment.getUser();
            LocalDateTime createdAt = finalComment.getCreatedAt();
            String comment = finalComment.getComment();

            if (user == null || createdAt == null || comment == null)
                return parseFinalComment(null);

            content = "<h4>%s</h4>"
                    + "<p style=\"color: grey;\">"
                    + "%s"
                    + "</p>"
                    + "<p style=\"margin-top: 15px; white-space: pre-line;\">"
                    + "%s"
                    + "</p>";
           content = String.format(content, user.getName(),
                    createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), comment);
        }

        return String.format(container, content);
    }

    /**
     * Send the email notifying the creator that the application has been approved/rejected
     * @param application the application approved/rejected
     */
    public void sendApplicationApprovalEmail(Application application) {
        String content = "<h2>Application Outcome - %s</h2>"
                + "<p>Hello %s,<br>This e-mail is a quick notification that the ethics committee has completed "
                + "the review process on your application</p>"
                + "<br>"
                + "<p>Application ID: <b>%s</b></p>"
                + "<p>Outcome: <b>%s</b></p>"
                + "%s"
                + "<br>"
                + "<h4>What do I need to do?</h4>"
                + "<p>You can view your application by clicking: <a href=\"%s\">%s</a></p>"
                + "<p>If for some reason, the link doesn't work, paste the following link into your browser: %s</p>"
                + "<br>"
                + "<p>%s</p>";

        User applicant = application.getUser();
        String applicationId = application.getApplicationId();
        boolean approved;
        ApplicationStatus status = application.getStatus();

        if (status == ApplicationStatus.APPROVED)
            approved = true;
        else if (status == ApplicationStatus.REJECTED)
            approved = false;
        else
            throw new InvalidStatusException("Can only send an outcome email for APPROVED or REJECTED statuses");

        String urlBase = getFrontendURL();
        urlBase = urlBase + "/application?id=" + applicationId;
        String email = applicant.getAccount().getEmail();
        String action = (approved) ? "You do not need to do anything as your application has been approved. However, " +
                "should anything change regarding your application, please update the ethics committee" :
                "We apologise that we had to reject the application at this time. Please review the comment left on the " +
                        "application and if you wish to still continue with the study, you can create a new application";

        content = String.format(content, applicationId, applicant.getName(), applicationId,
                (approved) ? ApplicationStatus.APPROVED.label() : ApplicationStatus.REJECTED.label(),
                parseFinalComment(application.getFinalComment()), urlBase, applicationId, urlBase, action);

        sendEmail(email, String.format("Application " + applicationId + " Review Outcome - %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))), content);
    }

    /**
     * Notifies a supervisor that they have been requested to answer some inputs on an application
     * @param request the created request
     */
    public void addAnswerInputRequested(AnswerRequest request) {
        String content = "<h2>Application Input Requested - %s</h2>"
                + "<p>Hello %s,<br>This e-mail is a quick notification that the applicant %s has "
                + "requested that you give input to some answers on their research ethics application form.</p>"
                + "<br>"
                + "<p>Application ID: <b>%s</b></p>"
                + "<p>Applicant: <b>%s</b></p>"
                + "<p>Requested At: <b>%s</b>"
                + "<br>"
                + "<h4>What do I need to do?</h4>"
                + "<p>You can answer the requested fields by following this link: <a href=\"%s\">%s</a></p>"
                + "<p>If for some reason, the link doesn't work, paste the following link into your browser: %s</p>";

        String requestedAt = request.getRequestedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        User user = request.getUser();
        Application application = request.getApplication();
        String requester = application.getUser().getName();
        String id = application.getApplicationId();

        String urlBase = getFrontendURL();
        urlBase = urlBase + (String.format("/answer-request?id=%d", request.getId()));

        content = String.format(content, requestedAt, user.getName(), requester, id,
                requester, requestedAt, urlBase, "Give Answers", urlBase);

        sendEmail(user.getAccount().getEmail(), String.format("Input Requested on Application %s at %s", application.getApplicationId(), requestedAt), content);
    }

    /**
     * Notifies an applicant that the supervisor has answered the requested questions that they have been requested to
     * answer some inputs on an application
     * @param request the resolved request
     */
    public void sendAnsweredResponse(AnswerRequest request) {
        String content = "<h2>Application Input Provided</h2>"
                + "<p>Hello %s,<br>This e-mail is a quick notification that %s has provided the"
                + " input to some answers you requested on your research ethics application form.</p>"
                + "<br>"
                + "<p>Application ID: <b>%s</b></p>"
                + "<p>User: <b>%s</b></p>"
                + "<p>Requested At: <b>%s</b>"
                + "<br>"
                + "<h4>What do I need to do?</h4>"
                + "<p>You can view your application by following this link: <a href=\"%s\">%s</a> and decide if it is" +
                " now ready to submit</p>"
                + "<p>If for some reason, the link doesn't work, paste the following link into your browser: %s</p>";

        String requestedAt = request.getRequestedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String user = request.getUser().getName();
        Application application = request.getApplication();
        String id = application.getApplicationId();
        User requester = application.getUser();

        String urlBase = getFrontendURL();
        urlBase = urlBase + (String.format("/application?id=%s", id));

        content = String.format(content, requester.getName(), user, id, user,
                requestedAt, urlBase, id, urlBase);

        sendEmail(requester.getAccount().getEmail(), String.format("Input on Application %s requested at %s provided", id, requestedAt), content);
    }
}
