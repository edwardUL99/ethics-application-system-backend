package ie.ul.edward.ethics.test.utils.constants;

/**
 * This class contains constants used for testing the users module
 */
public final class Users {
    /**
     * A name used for test user accounts
     */
    public static final String NAME = "Test User";

    /**
     * A department name used for test user accounts
     */
    public static final String DEPARTMENT = "Test Department";

    /**
     * The chair email for testing
     */
    public static final String CHAIR_EMAIL = "chair@email.com";

    /**
     * The property for setting the chair email for testing
     */
    public static final String CHAIR_EMAIL_PROPERTY = "permissions.chair=" + CHAIR_EMAIL;

    /**
     * The administrator email for testing
     */
    public static final String ADMINISTRATOR_EMAIL = "administrator@email.com";

    /**
     * The property for setting the administrator email for testing
     */
    public static final String ADMINISTRATOR_EMAIL_PROPERTY = "permissions.administrator=" + ADMINISTRATOR_EMAIL;
}
