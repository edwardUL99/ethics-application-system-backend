package ie.ul.edward.ethics.authentication.jwt;

import ie.ul.edward.ethics.authentication.exceptions.AuthenticationException;
import ie.ul.edward.ethics.authentication.models.Account;
import ie.ul.edward.ethics.authentication.models.AuthenticatedAccount;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public final class JwtAuthentication {
    /**
     * The secret key used for hashing/decrypting JWT tokens
     */
    private final Key secret;

    /**
     * The length of time the JWT Token is valid for
     */
    private static final long TOKEN_VALIDITY = 2;

    /**
     * The name of the environment variable to lookup for the secret key
     */
    public static final String ENV_SECRET_KEY = "ETHICS_AUTH_SECRET";

    /**
     * The name of the property key which is looked up if not found in the environment
     */
    public static final String PROPERTY_SECRET_KEY = "jwt.secret";

    /**
     * The secret key from properties
     */
    private final String propertySecret;

    /**
     * Creates a JwtAuthentication object
     * @throws AuthenticationException if the secret key cannot be found
     */
    @Autowired
    public JwtAuthentication(@Value("${" + PROPERTY_SECRET_KEY + "}") String propertySecret) {
        this.propertySecret = propertySecret;
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

        if (secret == null) {
            if (propertySecret != null)
                secret = propertySecret;

            if (secret == null)
                throw new AuthenticationException("Cannot find JWT secret key in environment or application properties");
        }

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
     * @return the generated JWT token
     */
    public String generateToken(Account account) {
        Map<String, Object> claims = new HashMap<>();

        LocalDateTime expiration = LocalDateTime.now().plusHours(TOKEN_VALIDITY);

        return Jwts.builder().setClaims(claims).setSubject(account.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(expiration.atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(secret).compact();
    }
}
