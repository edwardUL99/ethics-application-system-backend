package ie.ul.ethics.scieng.users.authorization;

import ie.ul.ethics.scieng.users.config.UserPermissionsConfig;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserServiceTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the permissions authorizer
 */
public class PermissionsAuthorizerTest {
    /**
     * The authorizer under test
     */
    private static PermissionsAuthorizer permissionsAuthorizer;

    /**
     * The configured paths
     */
    private static final Map<PermissionsAuthorizer.Path, RequiredPermissions> paths = new LinkedHashMap<>();

    /**
     * This is the test ant path 1
     */
    public static final String TEST_ANT_PATH1 = "/test/**/admin/**";

    /**
     * This is the test ant path 2
     */
    public static final String TEST_ANT_PATH2 = "/grant/permissions/**";

    /**
     * This is the test ant path 3
     */
    public static final String TEST_ANT_PATH3 = "/test/permissions/admin/submit/";

    static {
        paths.put(new PermissionsAuthorizer.Path(TEST_ANT_PATH1), new RequiredPermissions(Permissions.ADMIN));
        paths.put(new PermissionsAuthorizer.Path(TEST_ANT_PATH2, RequestMethod.PUT),
                new RequiredPermissions(List.of(Permissions.GRANT_PERMISSIONS), true));
        paths.put(new PermissionsAuthorizer.Path(TEST_ANT_PATH3),
                new RequiredPermissions(List.of(Permissions.GRANT_PERMISSIONS), true));

    }

    /**
     * Initialise the permissions authorizer instance
     */
    @BeforeAll
    private static void init() {
        permissionsAuthorizer = new PermissionsAuthorizer(paths);
    }

    /**
     * This tests that the user should be authorized for a given request and a path configured for ALL request methods
     */
    @Test
    public void shouldAuthorizeWithAllRequestMethods() {
        User user = UserServiceTest.createTestUser();
        user.setRole(Roles.ADMINISTRATOR);

        boolean authorized = permissionsAuthorizer.authorise("/test/auth/admin/account", "GET", user);

        assertTrue(authorized);
    }

    /**
     * Tests that if there is a path configured for a specific request method, it should only be authorized for that
     * request. Tests that any other request is authorized, even if permissions aren't met
     */
    @Test
    public void shouldAuthorizeWithSpecifiedRequestMethod() {
        User user = UserServiceTest.createTestUser();
        User user1 = UserServiceTest.createTestUser();

        user.setRole(Roles.ADMINISTRATOR);

        String path = TEST_ANT_PATH2;

        boolean authorized1 = permissionsAuthorizer.authorise(path, "PUT", user); // this should fail as it matches the specified request
        boolean authorized2 = permissionsAuthorizer.authorise(path, "GET", user1);

        assertFalse(authorized1);
        assertTrue(authorized2);
    }

    /**
     * Tests that if the path isn't matched, it should always authorize
     */
    @Test
    public void shouldAuthorizeWithPathNotConfigured() {
        User user = UserServiceTest.createTestUser();
        String path = "/path/not/configured/";

        boolean authorized = permissionsAuthorizer.authorise(path, "GET", user);

        assertTrue(authorized);
    }

    /**
     * Tests that if a path is configured, but user is null, authorization should not process
     */
    @Test
    public void shouldNotAuthorizeIfUserIsNull() {
        User user = null;

        boolean authorized = permissionsAuthorizer.authorise(TEST_ANT_PATH2, "PUT", user);

        assertFalse(authorized);
    }

    /**
     * Tests that a user should be authorized if multiple required permissions are retrieved from the path
     * if multiple required paths match and all permissions are satisfied
     */
    @Test
    public void shouldAuthorizeIfMultipleRequiredPermissionsFromPath() {
        User user = UserServiceTest.createTestUser();
        user.setRole(Roles.CHAIR);

        boolean returned = permissionsAuthorizer.authorise(TEST_ANT_PATH3, "GET", user);

        assertTrue(returned);
    }

    /**
     * Tests that a user should not be authorized if multiple required permissions are retrieved from the path
     * if multiple required paths match but some required permissions don't match
     */
    @Test
    public void shouldNotAuthorizeIfMultipleRequiredPermissionsFromPathAndOnePermissionMissing() {
        User user = UserServiceTest.createTestUser();
        user.setRole(Roles.ADMINISTRATOR);

        boolean returned = permissionsAuthorizer.authorise(TEST_ANT_PATH3, "GET", user);

        assertFalse(returned);
    }

    /**
     * If authorization is disabled, authorizer should always return true
     */
    @Test
    public void shouldAlwaysAuthorizeIfAuthorizationDisabled() {
        User user = UserServiceTest.createTestUser();

        Assertions.assertFalse(UserPermissionsConfig.permissionsDisabled()); // should authorize false when enabled

        boolean authorized = permissionsAuthorizer.authorise(TEST_ANT_PATH2, "PUT", user);

        assertFalse(authorized);

        System.setProperty(UserPermissionsConfig.USER_PERMISSIONS_DISABLED, "true");
        assertTrue(UserPermissionsConfig.permissionsDisabled()); // should authorize true when disabled

        authorized = permissionsAuthorizer.authorise(TEST_ANT_PATH2, "PUT", user);

        System.clearProperty(UserPermissionsConfig.USER_PERMISSIONS_DISABLED);
        assertFalse(UserPermissionsConfig.permissionsDisabled());

        assertTrue(authorized);
    }

    /**
     * Tests that the correct map for required permissions for the specified path is returned
     */
    @Test
    public void shouldGetRequiredPermissionsFromPath() {
        Map<String, List<RequiredPermissions>> expected = new HashMap<>();
        expected.put("ALL", List.of(new RequiredPermissions(Permissions.ADMIN)));

        Map<String, List<RequiredPermissions>> returned = permissionsAuthorizer.permissionsRequired("/test/auth/admin/account");

        assertEquals(expected, returned);
    }

    /**
     * Tests that if there are no permissions configured for the path, an empty map is returned
     */
    @Test
    public void shouldGetEmptyMapIfPathNotConfigured() {
        Map<String, List<RequiredPermissions>> returned = permissionsAuthorizer.permissionsRequired("/path/not/configured");
        assertEquals(returned.size(), 0);
    }

    /**
     * Tests that multiple required permissions should be retrieved from the path if multiple required paths match
     */
    @Test
    public void shouldGetMultipleRequiredPermissionsFromPath() {
        Map<String, List<RequiredPermissions>> expected = new HashMap<>();
        expected.put("ALL", List.of(new RequiredPermissions(Permissions.ADMIN),
                new RequiredPermissions(List.of(Permissions.GRANT_PERMISSIONS), true)));

        Map<String, List<RequiredPermissions>> returned = permissionsAuthorizer.permissionsRequired(TEST_ANT_PATH3);

        assertEquals(expected, returned);
    }
}
