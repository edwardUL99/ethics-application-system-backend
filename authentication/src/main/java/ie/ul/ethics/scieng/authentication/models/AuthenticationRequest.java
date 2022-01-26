package ie.ul.ethics.scieng.authentication.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * This class represents a request made for authentication. It is similar to an Account but ignores the email address
 */
@Getter
@Setter
public class AuthenticationRequest {
    /**
     * The username for the request. The username can be an email when the email flag is set to true
     */
    @NotBlank
    private String username;
    /**
     * This password is the plain text password given in the request
     */
    @NotBlank
    private String password;
    /**
     * This flag indicates if the provided username is in fact an email
     */
    @NotNull
    @Getter(AccessLevel.NONE)
    private boolean email;
    /**
     * This flag indicates the expiry (in hours), the issued authentication should be valid for
     */
    private Long expiry;

    /**
     * Construct a default AuthenticationRequest
     */
    public AuthenticationRequest() {
        this(null, null, null, null);
    }

    /**
     * Construct an AuthenticationRequest with the provided details
     * @param username the username for the request
     * @param password the password for the request
     * @param email the flag to indicate if the request should use the email for username lookup
     * @param expiry the expiry in hours the authentication should last for
     */
    public AuthenticationRequest(String username, String password, Boolean email, Long expiry) {
        this.username = username;
        this.password = password;
        setEmail(email);
        this.expiry = expiry;
    }

    /**
     * Sets the value for the email flag
     * @param email the email flag. If null, false is used
     */
    public void setEmail(Boolean email) {
        this.email = email != null && email;
    }

    /**
     * Determines if authentication should use email for username lookup
     * @return true to use email lookup, false to use username
     */
    public boolean isEmail() {
        return email;
    }

    /**
     * Get the expiry value.
     * @return Expiry value or -1 if default wants to be used
     */
    public long getExpiry() {
        return (expiry == null) ? -1L:expiry;
    }
}
