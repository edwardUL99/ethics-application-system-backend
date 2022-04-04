package ie.ul.ethics.scieng.common.email;

import ie.ul.ethics.scieng.common.email.exceptions.EmailException;
import ie.ul.ethics.scieng.common.properties.PropertyFinder;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is a base class which implements sendEmail as an asynchronous email sender. Can be extended by domain specific implementations
 */
public abstract class AsyncEmailService implements EmailService {
    /**
     * The email sender object to send the emails
     */
    private final EmailSender sender;

    /**
     * Instantiate the email service
     * @param sender the object to use for sending emails
     */
    protected AsyncEmailService(EmailSender sender) {
        this.sender = sender;
    }

    /**
     * Send the email to the specified recipient
     *
     * @param to          the email address of the recipient
     * @param subject     the subject to add to the email
     * @param email       the email to send
     * @param attachments a list of attachments to attach to the email
     */
    @Override
    public void sendEmail(String to, String subject, String email, File... attachments) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {
                sender.sendEmail(to, subject, email, attachments);
            } catch (EmailException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * Get the sender being used to send the emails
     *
     * @return the sender used for sending emails
     */
    @Override
    public EmailSender getSender() {
        return sender;
    }

    /**
     * Send an advanced email using the email service The default implementation is to use the sender and just send it
     *
     * @param advancedEmail the advanced email to send.
     */
    @Override
    public void sendEmail(AdvancedEmail advancedEmail) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.submit(() -> {
            try {
                EmailService.super.sendEmail(advancedEmail);
            } catch (EmailException ex) {
                ex.printStackTrace();
            }
        });
    }

    /**
     * This method gets the URL front-end base
     * @return the URL front-end base
     */
    protected String getFrontendURL() {
        String urlBase = PropertyFinder.findProperty("ETHICS_FRONTEND_URL", "frontend.url"); // find either by system/config property or environment variable

        return (urlBase == null) ? "http://localhost:4200" : urlBase;
    }
}
