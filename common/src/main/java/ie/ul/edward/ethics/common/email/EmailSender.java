package ie.ul.edward.ethics.common.email;

import ie.ul.edward.ethics.common.email.exceptions.EmailException;

import java.io.File;

/**
 * This interface represents an object that can send emails.
 */
public interface EmailSender {
    /**
     * Send the email to the specified recipient
     * @param to the email address of the recipient
     * @param subject the subject to add to the email
     * @param email the email to send
     * @param attachments a list of attachments to add to the email
     * @throws EmailException if an error occurs sending the email
     */
    void sendEmail(String to, String subject, String email, File...attachments) throws EmailException;
}
