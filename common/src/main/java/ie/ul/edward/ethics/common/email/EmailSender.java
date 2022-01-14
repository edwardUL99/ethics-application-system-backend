package ie.ul.edward.ethics.common.email;

import ie.ul.edward.ethics.common.email.exceptions.EmailException;

/**
 * This interface represents an object that can send emails.
 *
 * Email sending should be disabled if the system property email.disable is set
 */
public interface EmailSender {
    /**
     * Send the email to the specified recipient
     * @param to the email address of the recipient
     * @param subject the subject to add to the email
     * @param email the email to send
     * @throws EmailException if an error occurs sending the email
     */
    void sendEmail(String to, String subject, String email) throws EmailException;
}
