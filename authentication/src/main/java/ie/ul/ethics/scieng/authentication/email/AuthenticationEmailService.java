package ie.ul.ethics.scieng.authentication.email;

import ie.ul.ethics.scieng.authentication.config.AuthenticationConfiguration;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.authentication.models.ConfirmationToken;
import ie.ul.ethics.scieng.authentication.models.ResetPasswordToken;
import ie.ul.ethics.scieng.common.email.AsyncEmailService;
import ie.ul.ethics.scieng.common.email.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * This class provides the email service for the authentication module
 */
@Service("authenticationEmail")
public class AuthenticationEmailService extends AsyncEmailService {
    /**
     * Instantiate the email service
     *
     * @param sender the object to use for sending emails
     */
    @Autowired
    protected AuthenticationEmailService(EmailSender sender) {
        super(sender);
    }

    /**
     * Send the confirmation email to the email specified in the account
     * @param account the account to send the email to
     * @param confirmationToken the token for confirmation
     * @param authenticationConfiguration configuration properties for the confirmation email to query
     */
    public void sendConfirmationEmail(Account account, ConfirmationToken confirmationToken, AuthenticationConfiguration authenticationConfiguration) {
        String content = "<h2>Confirm Account</h2>"
                + "<p>Hello %s,<br>We have received a registration request for an account. You will need to confirm" +
                " the email address before we can proceed with registration</p>"
                + "<br>"
                + "<p>Your username is: <b>%s</b></p>"
                + "<p>Follow this link to confirm your account: <a href=\"%s\">Confirm Account</a></p>"
                + "<p>If for some reason, the link doesn't work, go to the <a href=\"%s\">confirm account</a> page"
                + " and enter the following details in the first 2 fields:</p>"
                + "<ul>"
                + "<li><b>E-mail:</b> %s</li>"
                + "<li><b>Confirmation Token:</b> %s</li>"
                + "</ul>"
                + "<p><b>Do not</b> give this token (or above link) to anybody else</p>"
                + "<br>"
                + "<p>If you did not request an account, you can safely ignore this e-mail</p>"
                + "<p><b>Warning:</b> Unconfirmed accounts will be removed after %d days</p>";

        String urlBase = getFrontendURL();
        urlBase = urlBase + "/confirm-account";

        String username = account.getUsername();
        String email = account.getEmail();
        String token = confirmationToken.getToken();
        content = String.format(content, username, username,
                String.format("%s?email=%s&token=%s", urlBase, email, token),
                urlBase, email, token, authenticationConfiguration.getUnconfirmedRemoval());

        sendEmail(email, String.format("Confirm Account Registration - %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))), content);
    }

    /**
     * Send the password reset email to the email specified in the account
     * @param account the account to send the email to
     * @param resetPasswordToken the token used for resetting the password
     */
    public void sendPasswordResetEmail(Account account, ResetPasswordToken resetPasswordToken) {
        String content = "<h2>Reset Password</h2>"
                + "<p>Hello %s,<br>We have received a request to reset the password of your account"
                + "<br>"
                + "<p>Your username is: <b>%s</b></p>"
                + "<p>Follow this link to reset your password: <a href=\"%s\">Reset Password</a></p>"
                + "<p>If for some reason, the link does not work, paste the following link into your browser: %s</p>"
                + "<p><b>Do not</b> give this token (or above link) to anybody else</p>"
                + "<br>"
                + "<p>This request will expire at <b>%s</b>, after which you will need to request another password reset</p>"
                + "<p>If you did not request for your password to be changed, you can safely ignore this e-mail</p>";

        String urlBase = getFrontendURL();
        urlBase = urlBase + "/reset-password";

        String username = account.getUsername();
        String email = account.getEmail();
        String resetLink = String.format("%s?username=%s&token=%s", urlBase, username, resetPasswordToken.getToken());
        content = String.format(content, username, username,
                resetLink, resetLink, resetPasswordToken.getExpiry().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        sendEmail(email, String.format("Reset Password - %s",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))), content);
    }
}
