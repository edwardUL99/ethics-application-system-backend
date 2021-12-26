package ie.ul.edward.ethics.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * This class provides configuration properties for authentication
 */
@Configuration
@ConfigurationProperties(prefix = "auth")
public class AuthenticationConfiguration {
    /**
     * The JWT config object
     */
    private Jwt jwt;

    /**
     * Create a default authentication configuration object
     */
    public AuthenticationConfiguration() {
        this.jwt = new Jwt();
    }

    /**
     * Get the Jwt config object
     * @return jwt config object
     */
    public Jwt getJwt() {
        return jwt;
    }

    /**
     * Set the Jwt config object to use
     * @param jwt the Jwt config object
     */
    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    /**
     * This class provides the JWT config
     */
    public static class Jwt {
        /**
         * The secret JWT key
         */
        private String secret;
        /**
         * The token config object
         */
        private Token token;

        /**
         * Creates a default Jwt
         */
        public Jwt() {
            this.secret = null;
            this.token = new Token();
        }

        /**
         * Get the JWT secret key
         * @return the secret key to use for JWT tokens
         * @throws IllegalStateException if a secret key is not setup
         */
        public String getSecret() {
            if (secret == null)
                throw new IllegalStateException("To use the authentication module, you have to set the secret key " +
                        "auth.jwt.secret in a properties file on the classpath");
            return secret;
        }

        /**
         * Sets the secret key to use for JWT
         * @param secret the new secret
         */
        public void setSecret(String secret) {
            this.secret = secret;
        }

        /**
         * Get the token config object
         * @return token config object
         */
        public Token getToken() {
            return token;
        }

        /**
         * Set the token object to use
         * @param token the new token config object
         */
        public void setToken(Token token) {
            this.token = token;
        }

        /**
         * This class provides the JWT Token config
         */
        public static class Token {
            /**
             * The validity of the token
             */
            private long validity;

            /**
             * Creates a default Token config object
             */
            public Token() {
                this.validity = 2;
            }

            /**
             * Get the validity of the token
             * @return the token validity
             */
            public long getValidity() {
                return validity;
            }

            /**
             * Sets the validity of the token in hours
             * @param validity the validity to use in hours
             */
            public void setValidity(long validity) {
                this.validity = validity;
            }
        }
    }
}
