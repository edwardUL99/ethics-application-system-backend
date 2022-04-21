package ie.ul.ethics.scieng.common.email;

import ie.ul.ethics.scieng.common.email.exceptions.EmailException;
import ie.ul.ethics.scieng.common.properties.PropertyFinder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an "advanced" email as a builder, i.e. an e-mail that is not just subtitle, content and attachments,
 * but contains nested images also
 */
public class AdvancedEmail {
    /**
     * The message representing the email
     */
    private final Message message;
    /**
     * The list of body parts in the e-mail message that aren't the e-mail body part
     */
    private final List<BodyPart> bodyParts;
    /**
     * The HTML content
     */
    private String htmlContent;
    /**
     * If true, the header with ethics committee title and UL logo will be added to e-mail content
     */
    private boolean addULHeader;
    /**
     * Determine if a generic footer should be added
     */
    private boolean addFooter;

    /**
     * Initialise the email with the given mail session
     * @param session the mail session
     */
    public AdvancedEmail(Session session) {
        this.message = new MimeMessage(session);
        this.bodyParts = new ArrayList<>();
    }

    /**
     * Set the from e-mail
     * @param from the e-mail address of the sender
     * @return instance of this for chaining
     * @throws EmailException if an error occurs
     */
    public AdvancedEmail setFrom(String from) {
        try {
            message.setFrom(new InternetAddress(from));

            return this;
        } catch (MessagingException ex) {
            throw new EmailException("Failed to set from", ex);
        }
    }

    /**
     * Adds the to participant of the e-mail
     * @param to the to e-mail address
     * @return instance of this for chaining
     * @throws EmailException if an error occurs
     */
    public AdvancedEmail setTo(String to) {
        return this.addRecipient(Message.RecipientType.TO, to);
    }

    /**
     * Add a recipient to the email
     * @param recipientType the type of the recipient
     * @param recipient the recipient address
     * @return instance of this for chaining
     * @throws EmailException if an error occurs
     */
    public AdvancedEmail addRecipient(Message.RecipientType recipientType, String recipient) {
        try {
            message.addRecipient(recipientType, new InternetAddress(recipient));

            return this;
        } catch (MessagingException ex) {
            throw new EmailException("Failed to add recipient", ex);
        }
    }

    /**
     * Set the subject of the e-mail
     * @param subject the e-mail subject
     * @return the e-mail subject
     * @throws EmailException if an error occurs
     */
    public AdvancedEmail setSubject(String subject) {
        try {
            message.setSubject(subject);

            return this;
        } catch (MessagingException ex) {
            throw new EmailException("Failed to set subject", ex);
        }
    }

    /**
     * Set the HTML content for the e-mail
     * @param html HTML content
     * @param addULHeader true to add header with committee title and UL logo
     * @param addFooter determine if the footer should be added
     * @return instance of this for chaining
     */
    public AdvancedEmail setContent(String html, boolean addULHeader, boolean addFooter) {
        this.htmlContent = html;
        this.addULHeader = addULHeader;
        this.addFooter = addFooter;

        return this;
    }

    /**
     * Attach the file to the e-mail
     * @param file the file to attach
     * @return instance of this for chaining
     * @throws EmailException if an error occurs
     */
    public AdvancedEmail attachFile(File file) {
        try {
            MimeBodyPart filePart = new MimeBodyPart();
            filePart.attachFile(file);
            bodyParts.add(filePart);

            return this;
        } catch (MessagingException | IOException ex) {
            throw new EmailException("Failed to attach file", ex);
        }
    }

    /**
     * Add the image to the e-mail
     * @param imageID the ID mentioned in the html content as <img src="cid:imageID">
     * @param image the image resource
     * @return instance of this for chaining
     * @throws EmailException if an error occurs
     */
    public AdvancedEmail attachImage(String imageID, Resource image) {
        try {
            BodyPart bodyPart = new MimeBodyPart();
            DataSource fds = new URLDataSource(image.getURL());
            bodyPart.setDataHandler(new DataHandler(fds));
            bodyPart.setHeader("Content-ID", "<" + imageID + ">");
            bodyPart.setFileName(imageID);
            bodyParts.add(bodyPart);

            return this;
        } catch (MessagingException | IOException ex) {
            throw new EmailException("Failed to attach image", ex);
        }
    }

    /**
     * Get the header HTML
     * @return header html containing title and UL logo
     */
    private String getHeaderHTML() {
        return "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                "<tr>" +
                "<td align=\"center\">" +
                "<h2>University of Limerick Faculty of Science and Engineering Research Ethics Committee</h2>" +
                "</td>" +
                "</tr>" +
                "<tr>" +
                "<td align=\"center\">" +
                "<img src=\"cid:ul-logo\">" +
                "</td>" +
                "</tr>" +
                "</table>";
    }

    /**
     * Get the HTML for the footer
     * @return the footer HTML
     */
    private String getFooterHTML() {
        String footer = "<br>" +
                "<p>Thank You,<p>" +
                "<p>The Committee</p>" +
                "<br><hr>" +
                "<table width=\"100%%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                "<tr>" +
                "<td align=\"center\" style=\"color: gray;\">" +
                "%s" +
                "</td>" +
                "</tr>" +
                "</table>";

        String contactEmail = PropertyFinder.findProperty("ETHICS_EMAIL_CONTACT", "email.contact");
        String footerContent = String.format("<p>Please do not reply to this e-mail. Should you have the need to contact" +
                " the committee, please contact <a href=\"mailto:%s\">%s</a></p>", contactEmail, contactEmail);

        return String.format(footer, footerContent);
    }

    /**
     * Parse the HTML content into a BodyPart
     * @return parsed body part
     * @throws EmailException if the part fails to be parsed
     */
    private BodyPart parseHTMLContent() {
        String content = this.htmlContent;

        if (this.addULHeader) {
            content = getHeaderHTML() + content;

            Resource resource = new ClassPathResource("ul-logo.jpg");
            attachImage("ul-logo", resource);
        }

        if (this.addFooter)
            content += getFooterHTML();

        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html");

            return mimeBodyPart;
        } catch (MessagingException ex) {
            throw new EmailException("Failed to construct HTML content", ex);
        }
    }

    /**
     * The final call to build the e-mail
     * @return the built e-mail message
     * @throws EmailException if an error occurs
     */
    public Message buildMessage() {
        try {
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(parseHTMLContent());

            for (BodyPart bodyPart : this.bodyParts)
                multipart.addBodyPart(bodyPart);

            message.setContent(multipart);

            return message;
        } catch (MessagingException ex) {
            throw new EmailException("Failed to construct message", ex);
        }
    }
}
