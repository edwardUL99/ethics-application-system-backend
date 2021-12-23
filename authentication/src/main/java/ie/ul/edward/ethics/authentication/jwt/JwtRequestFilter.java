package ie.ul.edward.ethics.authentication.jwt;

import ie.ul.edward.ethics.authentication.exceptions.AuthenticationException;
import ie.ul.edward.ethics.authentication.models.Account;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
    private final JwtAuthentication jwtAuthentication;

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
     * @param jwtAuthentication the authentication object for JWT utilities
     */
    @Autowired
    public JwtRequestFilter(UserDetailsService accountService, JwtAuthentication jwtAuthentication) {
        this.accountService = accountService;
        this.jwtAuthentication = jwtAuthentication;
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

        Account authenticated = null;
        String token;

        if (tokenHeader != null && tokenHeader.startsWith(BEARER)) {
            token = tokenHeader.substring(BEARER.length());

            try {
                if (!jwtAuthentication.isTokenExpired(token))
                    authenticated = jwtAuthentication.getAuthenticatedAccount(token);
            } catch (AuthenticationException ex) {
                log.error("An error occurred filtering the JWT request", ex);
            }
        } else if (tokenHeader != null) {
            throw new ServletException("JWT Authorization token needs to start with " + BEARER);
        }

        if (authenticated != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = accountService.loadUserByUsername(authenticated.getUsername());

            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            userToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(userToken);
        }

        filterChain.doFilter(request, response);
    }
}
