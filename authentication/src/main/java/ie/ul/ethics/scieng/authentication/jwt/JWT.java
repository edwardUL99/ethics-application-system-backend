package ie.ul.ethics.scieng.authentication.jwt;

import ie.ul.ethics.scieng.authentication.config.AuthenticationConfiguration;
import ie.ul.ethics.scieng.authentication.exceptions.AuthenticationException;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.authentication.models.AuthenticatedAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This class provides utility functions for handling the authentication of requests using JWT
 */
@Component
public class JWT {
    /**
     * The secret key used for hashing/decrypting JWT tokens
     */
    private final Key secret;

    /**
     * The name of the environment variable to lookup for the secret key
     */
    public static final String ENV_SECRET_KEY = "ETHICS_AUTH_SECRET";

    /**
     * The authentication configuration
     */
    private final AuthenticationConfiguration authConfig;

    /**
     * Creates a JWT object
     * @throws AuthenticationException if the secret key cannot be found
     */
    @Autowired
    public JWT(AuthenticationConfiguration authConfig) {
        this.authConfig = authConfig;
        this.secret = initialiseSecret();
    }

    /**
     * Initialises the secret key by performing a lookup in the environment. If not found in the environment, it will
     * look in the property file. If not found there, it is looked for in properties.
     * If spring environment is null, it uses Java System properties
     * @return the secret
     * @throws AuthenticationException if the secret can't be found
     */
    private Key initialiseSecret() throws AuthenticationException {
        String secret = System.getenv(ENV_SECRET_KEY);

        if (secret == null)
            secret = authConfig.getJwt().getSecret();

        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Get the JWT Claims parsed from the token
     * @param token the token to parse
     * @return the parsed Claims
     */
    private Claims getTokenClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    /**
     * Get the account represented by the provided token
     * @param token the token to parse into an AuthenticatedAccount instance
     * @return the account representing the authenticated user (instance of {@link AuthenticatedAccount})
     */
    public Account getAuthenticatedAccount(String token) {
        try {
            Claims claims = getTokenClaims(token);
            String username = claims.getSubject();
            Date expiration = claims.getExpiration();
            LocalDateTime localDate = expiration.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return new AuthenticatedAccount(username, token, localDate);
        } catch (JwtException e) {
            throw new AuthenticationException("The provided token is not a valid token. Is the token malformed or expired?");
        }
    }

    /**
     * Checks if the provided token is expired
     * @param token the token to check
     * @return true if expired, false if not
     */
    public boolean isTokenExpired(String token) {
        try {
            getTokenClaims(token);

            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            throw new AuthenticationException("An error occurred checking if the provided token is expired", e);
        }
    }

    /**
     * Generate a JWT Token for the provided account
     * @param account the account to generate the token for
     * @param expiry, the time in hours to expire the token at. If null, a default value will be used
     * @return the generated JWT token
     */
    public String generateToken(Account account, Long expiry) {
        Map<String, Object> claims = new HashMap<>();

        expiry = (expiry == null || expiry == -1L) ? authConfig.getJwt().getToken().getValidity():expiry;

        LocalDateTime expiration = LocalDateTime.now().plusHours(expiry);

        return Jwts.builder().setClaims(claims).setSubject(account.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(secret).compact();
    }
}
