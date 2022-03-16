package ie.ul.ethics.scieng.common.email;

import java.io.File;

/**
 * This interface represents a service that abstracts sending of emails by encapsulating the EmailSender
 */
public interface EmailService {
    /**
     * Send the email to the specified recipient
     *
     * @param to    the email address of the recipient
     * @param subject the subject to add to the email
     * @param email the email to send
     * @param attachments a list of attachments to attach to the email
     */
    void sendEmail(String to, String subject, String email, File...attachments);
}
