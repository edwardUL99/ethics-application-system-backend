package ie.ul.edward;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static ie.ul.edward.Constants.Endpoint;

/**
 * Provides unit tests for the Constants class
 */
public class ConstantsTest {
    /**
     * Tests that the Constants method for creating an endpoint url works with no arguments
     */
    @Test
    public void shouldCreateApiPath() {
        Endpoint[] endpoints = Endpoint.values();

        for (Endpoint endpoint : endpoints) {
            String expected = "/api/" + endpoint.endpoint() + "/";

            assertEquals(expected, Constants.createApiPath(endpoint));
        }
    }

    /**
     * Tests that the Constants method for creating and endpoint url works with additional arguments
     */
    @Test
    public void shouldCreateApiPathWithArguments() {
        Endpoint[] endpoints = Endpoint.values();

        for (Endpoint endpoint : endpoints) {
            String expected = "/api/" + endpoint.endpoint() + "/login/login1/";

            assertEquals(expected, Constants.createApiPath(endpoint, "login", "login1"));
        }
    }
}
