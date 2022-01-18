package ie.ul.edward.ethics.applications.services;

import ie.ul.edward.ethics.applications.models.applications.Application;
import ie.ul.edward.ethics.applications.models.applications.ApplicationStatus;
import ie.ul.edward.ethics.applications.models.applications.DraftApplication;
import ie.ul.edward.ethics.applications.repositories.ApplicationRepository;
import ie.ul.edward.ethics.applications.templates.ApplicationTemplate;
import ie.ul.edward.ethics.applications.templates.ApplicationTemplateLoader;
import ie.ul.edward.ethics.applications.templates.repositories.ApplicationTemplateRepository;
import ie.ul.edward.ethics.authentication.models.Account;
import ie.ul.edward.ethics.test.utils.Caching;
import ie.ul.edward.ethics.users.authorization.Roles;
import ie.ul.edward.ethics.users.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static ie.ul.edward.ethics.test.utils.constants.Authentication.*;
import static ie.ul.edward.ethics.test.utils.constants.Users.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * This class tests the application service
 */
@SpringBootTest(classes = {
        ie.ul.edward.ethics.test.utils.TestApplication.class,
        ie.ul.edward.ethics.applications.test.config.TestConfiguration.class,
        ie.ul.edward.ethics.applications.templates.config.TemplatesConfiguration.class,
        ie.ul.edward.ethics.authentication.jwt.JWT.class,
        ie.ul.edward.ethics.authentication.jwt.JwtRequestFilter.class
}, properties = {
        "auth.jwt.secret=ethics-secret-hashing-key-thirty-five-characters-long",
        "auth.jwt.token.validity=2",
        "permissions.authorization.enabled=true"
})
public class ApplicationServiceTest {
    /**
     * The mock template repository
     */
    @MockBean
    private ApplicationTemplateRepository templateRepository;
    /**
     * The mock application repository
     */
    @MockBean
    private ApplicationRepository applicationRepository;
    /**
     * The application service being tested
     */
    @Autowired
    private ApplicationService applicationService;
    /**
     * The cache utilities so we can evict cache for testing
     */
    @Autowired
    private Caching cache;
    /**
     * The array of templates to be used for testing
     */
    private final ApplicationTemplate[] templates;

    /**
     * The test application database ID
     */
    private static final Long APPLICATION_DB_ID = 1L;

    /**
     * The test ethics application ID
     */
    private static final String APPLICATION_ID = "app_id";

    /**
     * The test template database ID
     */
    private static final Long TEMPLATE_DB_ID = 2L;

    /**
     * Create the test class and load the templates
     * @param templateLoader the loader to load the templates with
     */
    @Autowired
    public ApplicationServiceTest(ApplicationTemplateLoader templateLoader) {
        this.templates = templateLoader.loadTemplates();
    }

    /**
     * Clear cache before each test
     */
    @BeforeEach
    private void clearCache() {
        cache.clearCache();
    }

    /**
     * Get a test application template
     * @return a mock application template for testing
     */
    private ApplicationTemplate getTemplate() {
        return templates[0];
    }

    /**
     * Creates a test account
     * @return the test account
     */
    public static Account createTestAccount() {
        return new Account(USERNAME, EMAIL, PASSWORD, false);
    }

    /**
     * Creates a test user
     * @return the test user
     */
    public static User createTestUser() {
        return new User(NAME, createTestAccount(), DEPARTMENT, Roles.APPLICANT);
    }

    /**
     * Create a test application. We will use a DraftApplication for testing
     * @return the test application
     */
    public Application createDraftApplication() {
        HashMap<String, DraftApplication.Value> values = new HashMap<>();
        String[] components = {"component1", "component2", "component3", "component4"};
        String[] answers = {"answer1", "answer2", "answer3", "answer4"};

        for (int i = 0; i < components.length; i++) {
            String id = components[i];
            values.put(id, new DraftApplication.Value(null, id, answers[i], DraftApplication.ValueType.TEXT));
        }

        return new DraftApplication(APPLICATION_DB_ID, APPLICATION_ID, createTestUser(), getTemplate(), values);
    }

    /**
     * This tests that applications should be retrieved successfully
     */
    @Test
    public void shouldGetApplication() {
        Application application = createDraftApplication();

        given(applicationRepository.findById(APPLICATION_DB_ID))
                .willReturn(Optional.of(application));

        Application found = applicationService.getApplication(APPLICATION_DB_ID);

        assertEquals(application, found);
        verify(applicationRepository).findById(APPLICATION_DB_ID);
    }

    /**
     * Tests that retrieving an application should be cached
     */
    @Test
    public void shouldGetApplicationCache() {
        Application application = createDraftApplication();

        given(applicationRepository.findById(APPLICATION_DB_ID))
                .willReturn(Optional.of(application));

        applicationService.getApplication(APPLICATION_DB_ID);
        applicationService.getApplication(APPLICATION_DB_ID);
        applicationService.getApplication(APPLICATION_DB_ID);
        Application found = applicationService.getApplication(APPLICATION_DB_ID);

        assertEquals(application, found);
        verify(applicationRepository, times(1)).findById(APPLICATION_DB_ID);
    }

    /**
     * Tests that null should be returned if an application by ID does not exist
     */
    @Test
    public void shouldReturnNullOnGetApplicationNotFound() {
        given(applicationRepository.findById(APPLICATION_DB_ID))
                .willReturn(Optional.empty());

        Application found = applicationService.getApplication(APPLICATION_DB_ID);

        assertNull(found);
        verify(applicationRepository).findById(APPLICATION_DB_ID);
    }

    /**
     * Tests that applications created by user should be retrieved
     */
    @Test
    public void shouldGetUserApplications() {
        Application application = createDraftApplication();
        User user = application.getUser();
        List<Application> applications = List.of(application);

        given(applicationRepository.findByUser(user))
                .willReturn(applications);

        List<Application> retrieved = applicationService.getUserApplications(user);

        assertEquals(applications, retrieved);
        verify(applicationRepository).findByUser(user);
    }

    /**
     * Tests that retrieving use's applications should be cached
     */
    @Test
    public void shouldGetUserApplicationsCache() {
        Application application = createDraftApplication();
        User user = application.getUser();
        List<Application> applications = List.of(application);

        given(applicationRepository.findByUser(user))
                .willReturn(applications);

        applicationService.getUserApplications(user);
        applicationService.getUserApplications(user);
        applicationService.getUserApplications(user);
        List<Application> retrieved = applicationService.getUserApplications(user);

        assertEquals(applications, retrieved);
        verify(applicationRepository, times(1)).findByUser(user);
    }

    /**
     * Tests that applications are retrieved by status
     */
    @Test
    public void shouldGetApplicationsWithStatus() {
        Application application = createDraftApplication();
        ApplicationStatus status = ApplicationStatus.DRAFT;
        List<Application> applications = List.of(application);

        given(applicationRepository.findByStatus(status))
                .willReturn(applications);

        List<Application> retrieved = applicationService.getApplicationsWithStatus(status);

        assertEquals(applications, retrieved);
        verify(applicationRepository).findByStatus(status);
    }

    /**
     * Tests that retrieving use's applications should be cached
     */
    @Test
    public void shouldGetApplicationsByStatusCache() {
        Application application = createDraftApplication();
        ApplicationStatus status = ApplicationStatus.DRAFT;
        List<Application> applications = List.of(application);

        given(applicationRepository.findByStatus(status))
                .willReturn(applications);

        applicationService.getApplicationsWithStatus(status);
        applicationService.getApplicationsWithStatus(status);
        applicationService.getApplicationsWithStatus(status);
        List<Application> retrieved = applicationService.getApplicationsWithStatus(status);

        assertEquals(applications, retrieved);
        verify(applicationRepository, times(1)).findByStatus(status);
    }

    /**
     * Tests that the application should be created
     */
    @Test
    public void shouldCreateApplication() {
        Application application = createDraftApplication();

        Application created = applicationService.createApplication(application);

        assertEquals(application, created);
        assertNotNull(application.getLastUpdated());
        verify(applicationRepository).save(application);
    }

    /**
     * Tests that a draft application should be created
     */
    @Test
    public void shouldCreateDraftApplication() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication();

        Application created = applicationService.createDraftApplication(draftApplication, false);

        assertEquals(draftApplication, created);
        assertNotNull(created.getLastUpdated());
        verify(templateRepository).save(draftApplication.getApplicationTemplate());
        verify(applicationRepository).save(draftApplication);
    }

    /**
     * Tests that a draft application should be updated
     */
    @Test
    public void shouldUpdateDraftApplication() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication();
        LocalDateTime now = LocalDateTime.now();
        draftApplication.setLastUpdated(now);

        Application created = applicationService.createDraftApplication(draftApplication, true);

        assertEquals(draftApplication, created);
        assertTrue(created.getLastUpdated() != null && created.getLastUpdated().isAfter(now));
        verifyNoInteractions(templateRepository);
        verify(applicationRepository).save(draftApplication);
    }

    /**
     * Tests that an IllegalStateException is thrown if an application is attempted to be updated without an ID
     */
    @Test
    public void shouldThrowIllegalStateOnDraftUpdateNoId() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication();
        draftApplication.setId(null);

        assertThrows(IllegalStateException.class, () -> applicationService.createDraftApplication(draftApplication, true));

        verifyNoInteractions(templateRepository);
        verify(applicationRepository, times(0)).save(draftApplication);
    }

    /**
     * Tests that the application template should be retrieved successfully
     */
    @Test
    public void shouldGetApplicationTemplate() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication();
        ApplicationTemplate template = draftApplication.getApplicationTemplate();
        template.setDatabaseId(TEMPLATE_DB_ID);

        given(templateRepository.findById(TEMPLATE_DB_ID))
                .willReturn(Optional.of(template));

        ApplicationTemplate returned = applicationService.getApplicationTemplate(TEMPLATE_DB_ID);

        assertEquals(template, returned);
        verify(templateRepository).findById(TEMPLATE_DB_ID);
    }

    /**
     * Tests that the application template should be retrieved successfully from cache
     */
    @Test
    public void shouldGetApplicationTemplateCached() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication();
        ApplicationTemplate template = draftApplication.getApplicationTemplate();
        template.setDatabaseId(TEMPLATE_DB_ID);

        given(templateRepository.findById(TEMPLATE_DB_ID))
                .willReturn(Optional.of(template));

        applicationService.getApplicationTemplate(TEMPLATE_DB_ID);
        applicationService.getApplicationTemplate(TEMPLATE_DB_ID);
        applicationService.getApplicationTemplate(TEMPLATE_DB_ID);
        ApplicationTemplate returned = applicationService.getApplicationTemplate(TEMPLATE_DB_ID);

        assertEquals(template, returned);
        verify(templateRepository, times(1)).findById(TEMPLATE_DB_ID);
    }

    /**
     * Tests that null should be returned if no template with a given ID is found
     */
    @Test
    public void shouldReturnNullOnTemplateNotFound() {
        given(templateRepository.findById(TEMPLATE_DB_ID))
                .willReturn(Optional.empty());

        ApplicationTemplate returned = applicationService.getApplicationTemplate(TEMPLATE_DB_ID);

        assertNull(returned);
        verify(templateRepository).findById(TEMPLATE_DB_ID);
    }
}
