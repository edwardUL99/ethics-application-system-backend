package ie.ul.edward.ethics.common.email;

import ie.ul.edward.ethics.common.email.exceptions.EmailException;
import lombok.extern.log4j.Log4j2;

import java.io.File;

/**
 * This class provides a noop implementation of the EmailSender interface. Useful for when email sending may wish to be disabled,
 * but to have that fact transparent to the clients
 */
@Log4j2
public class NoopEmailSender implements EmailSender {
    /**
     * Send the email to the specified recipient
     *
     * @param to          the email address of the recipient
     * @param subject     the subject to add to the email
     * @param email       the email to send
     * @param attachments a list of attachments to add to the email
     * @throws EmailException if an error occurs sending the email
     */
    @Override
    public void sendEmail(String to, String subject, String email, File... attachments) throws EmailException {
        log.info("No-op implementation of EmailSender. Not sending email");
    }
}
