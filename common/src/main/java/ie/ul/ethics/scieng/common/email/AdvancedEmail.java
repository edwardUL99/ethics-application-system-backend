package ie.ul.ethics.scieng.common.email;

import ie.ul.ethics.scieng.common.email.exceptions.EmailException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
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
    private List<BodyPart> bodyParts;
    /**
     * The HTML content
     */
    private String htmlContent;
    /**
     * If true, the header with ethics committee title and UL logo will be added to e-mail content
     */
    private boolean addULHeader;

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
     * @return instance of this for chaining
     */
    public AdvancedEmail setContent(String html, boolean addULHeader) {
        this.htmlContent = html;
        this.addULHeader = addULHeader;

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
     * @param image the image file to attach
     * @return instance of this for chaining
     * @throws EmailException if an error occurs
     */
    public AdvancedEmail attachImage(String imageID, File image) {
        try {
            BodyPart bodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(image);
            bodyPart.setDataHandler(new DataHandler(fds));
            bodyPart.setHeader("Content-ID", "<" + imageID + ">");
            bodyPart.setFileName(image.getName());
            bodyParts.add(bodyPart);

            return this;
        } catch (MessagingException ex) {
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
                "<h3>University of Limerick Faculty of Science and Engineering Research Ethics Committee</h3>" +
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
     * Parse the HTML content into a BodyPart
     * @return parsed body part
     * @throws EmailException if the part fails to be parsed
     */
    private BodyPart parseHTMLContent() {
        String content = this.htmlContent;

        if (this.addULHeader) {
            content = getHeaderHTML() + content;

            try {
                Resource resource = new ClassPathResource("ul-logo.jpg");
                File file = resource.getFile();
                attachImage("ul-logo", file);
            } catch (IOException ex) {
                throw new EmailException("Failed to construct HTML content", ex);
            }
        }

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
