package ie.ul.ethics.scieng.common.email;

import ie.ul.ethics.scieng.common.email.exceptions.EmailException;

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

    /**
     * Send the advanced email by building it and sending it
     * @param advancedEmail the email to build and send
     * @throws EmailException if an error occurs sending the email
     */
    void sendEmail(AdvancedEmail advancedEmail) throws EmailException;

    /**
     * Instantiate an instance of AdvancedEmail by passing in an instance of Session
     * @return the advanced email instance
     */
    AdvancedEmail createAdvancedEmail();
}
