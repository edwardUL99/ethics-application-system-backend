package ie.ul.ethics.scieng.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Provides unit tests for the Constants class
 */
public class ConstantsTest {
    /**
     * Tests that the Constants method for creating an endpoint url works with no arguments
     */
    @Test
    public void shouldCreateApiPath() {
        Constants.Endpoint[] endpoints = Constants.Endpoint.values();

        for (Constants.Endpoint endpoint : endpoints) {
            String expected = "/api/" + endpoint.endpoint() + "/";

            assertEquals(expected, Constants.createApiPath(endpoint));
        }
    }

    /**
     * Tests that the Constants method for creating and endpoint url works with additional arguments
     */
    @Test
    public void shouldCreateApiPathWithArguments() {
        Constants.Endpoint[] endpoints = Constants.Endpoint.values();

        for (Constants.Endpoint endpoint : endpoints) {
            String expected = "/api/" + endpoint.endpoint() + "/login/login1/";

            assertEquals(expected, Constants.createApiPath(endpoint, "login", "login1"));
        }
    }
}
