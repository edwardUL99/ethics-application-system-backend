package ie.ul.edward.ethics.authentication.jwt;

import ie.ul.edward.ethics.authentication.exceptions.AuthenticationException;
import ie.ul.edward.ethics.authentication.models.AuthenticatedAccount;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This filter provides the required processing for JWT processing
 */
@Component
@Log4j2
public class JwtRequestFilter extends OncePerRequestFilter {
    /**
     * The service for accessing accounts
     */
    private final UserDetailsService accountService;
    /**
     * The authentication utility class providing Jwt authentication
     */
    private final JWT jwt;
    @Resource(name="authenticationInformation")
    private AuthenticationInformation authenticationInformation;

    /**
     * The authorization header
     */
    private static final String AUTHORIZATION = "Authorization";

    /**
     * The bearer header prefix
     */
    private static final String BEARER = "Bearer ";

    /**
     * Construct a filter for JWT processing
     * @param accountService the service providing account access
     * @param jwt the authentication object for JWT utilities
     */
    @Autowired
    public JwtRequestFilter(UserDetailsService accountService, JWT jwt) {
        this.accountService = accountService;
        this.jwt = jwt;
    }

    /**
     * Performs the filtering of the headers for the Authorization: Bearer token
     * @param request the request object
     * @param response the response object
     * @param filterChain the chain of filters
     * @throws ServletException if an error occurs
     * @throws IOException if an error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader(AUTHORIZATION);

        AuthenticatedAccount authenticated = null;
        String token = null;

        if (tokenHeader != null && tokenHeader.startsWith(BEARER)) {
            token = tokenHeader.substring(BEARER.length());

            try {
                if (!jwt.isTokenExpired(token))
                    authenticated = (AuthenticatedAccount) jwt.getAuthenticatedAccount(token);
                else
                    log.error("Failed to authenticate with JWT due to an expired token");
            } catch (AuthenticationException ex) {
                log.error("An error occurred filtering the JWT request", ex);
            }
        } else if (tokenHeader != null) {
            throw new ServletException("JWT Authorization token needs to start with " + BEARER);
        }

        if (authenticated != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String username = authenticated.getUsername();

            UserDetails userDetails = accountService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(userToken);

            authenticationInformation.setToken(token);
            authenticationInformation.setExpiry(authenticated.getExpiration());
            authenticationInformation.setUsername(username);
        }

        filterChain.doFilter(request, response);
    }
}
