package ie.ul.ethics.scieng.applications.services;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.applications.repositories.ApplicationRepository;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;
import ie.ul.ethics.scieng.applications.templates.repositories.ApplicationTemplateRepository;
import ie.ul.ethics.scieng.authentication.jwt.JWT;
import ie.ul.ethics.scieng.authentication.jwt.JwtRequestFilter;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.test.utils.Caching;
import ie.ul.ethics.scieng.users.authorization.Roles;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.applications.templates.config.TemplatesConfiguration;
import ie.ul.ethics.scieng.applications.test.config.TestConfiguration;
import ie.ul.ethics.scieng.test.utils.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static ie.ul.ethics.scieng.test.utils.constants.Users.*;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * This class tests the application service
 */
@SpringBootTest(classes = {
        TestApplication.class,
        TestConfiguration.class,
        TemplatesConfiguration.class,
        JWT.class,
        JwtRequestFilter.class
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
     * The mock loader bean
     */
    @MockBean
    private ApplicationTemplateLoader loader;
    /**
     * The array of templates to be used for testing
     */
    private final ApplicationTemplate[] templates;

    /**
     * The test application database ID
     */
    public static final Long APPLICATION_DB_ID = 1L;

    /**
     * The test ethics application ID
     */
    public static final String APPLICATION_ID = "app_id";

    /**
     * The test template database ID
     */
    public static final Long TEMPLATE_DB_ID = 2L;

    /**
     * Create the test class and load the templates
     */
    public ApplicationServiceTest() {
        ApplicationTemplate template = new ApplicationTemplate(null, "test", "test app", "description", "1.0", new ArrayList<>());
        this.templates = new ApplicationTemplate[]{template};
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
    public static Application createDraftApplication(ApplicationTemplate applicationTemplate) {
        HashMap<String, Answer> values = new HashMap<>();
        String[] components = {"component1", "component2", "component3", "component4"};
        String[] answers = {"answer1", "answer2", "answer3", "answer4"};

        for (int i = 0; i < components.length; i++) {
            String id = components[i];
            values.put(id, new Answer(null, id, answers[i], Answer.ValueType.TEXT));
        }

        return new DraftApplication(APPLICATION_DB_ID, APPLICATION_ID, createTestUser(), applicationTemplate, values);
    }

    /**
     * Create a submitted application from the provided draft application
     * @param draftApplication the draft application to submit
     * @return the submitted application
     */
    public static Application createSubmittedApplication(DraftApplication draftApplication) {
        return new SubmittedApplication(null, draftApplication.getApplicationId(), draftApplication.getUser(),
                ApplicationStatus.SUBMITTED, draftApplication.getApplicationTemplate(), draftApplication.getAnswers(),
                new ArrayList<>(), new ArrayList<>(), null);
    }

    /**
     * This tests that applications should be retrieved successfully
     */
    @Test
    public void shouldGetApplication() {
        Application application = createDraftApplication(getTemplate());

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
        Application application = createDraftApplication(getTemplate());

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
     * This tests that applications should be retrieved successfully
     */
    @Test
    public void shouldGetApplicationByAppId() {
        Application application = createDraftApplication(getTemplate());
        application.setApplicationId(APPLICATION_ID);

        given(applicationRepository.findByApplicationId(APPLICATION_ID))
                .willReturn(Optional.of(application));

        Application found = applicationService.getApplication(APPLICATION_ID);

        assertEquals(application, found);
        verify(applicationRepository).findByApplicationId(APPLICATION_ID);
    }

    /**
     * Tests that retrieving an application should be cached
     */
    @Test
    public void shouldGetApplicationByAppIdCache() {
        Application application = createDraftApplication(getTemplate());
        application.setApplicationId(APPLICATION_ID);

        given(applicationRepository.findByApplicationId(APPLICATION_ID))
                .willReturn(Optional.of(application));

        applicationService.getApplication(APPLICATION_ID);
        applicationService.getApplication(APPLICATION_ID);
        applicationService.getApplication(APPLICATION_ID);
        Application found = applicationService.getApplication(APPLICATION_ID);

        assertEquals(application, found);
        verify(applicationRepository, times(1)).findByApplicationId(APPLICATION_ID);
    }

    /**
     * Tests that null should be returned if an application by ID does not exist
     */
    @Test
    public void shouldReturnNullOnGetApplicationNotFoundByAppId() {
        given(applicationRepository.findByApplicationId(APPLICATION_ID))
                .willReturn(Optional.empty());

        Application found = applicationService.getApplication(APPLICATION_ID);

        assertNull(found);
        verify(applicationRepository).findByApplicationId(APPLICATION_ID);
    }

    /**
     * Tests that applications created by user should be retrieved
     */
    @Test
    public void shouldGetUserApplications() {
        Application application = createDraftApplication(getTemplate());
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
        Application application = createDraftApplication(getTemplate());
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
        Application application = createDraftApplication(getTemplate());
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
        Application application = createDraftApplication(getTemplate());
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
        Application application = createDraftApplication(getTemplate());

        Application created = applicationService.createApplication(application, false);

        assertEquals(application, created);
        assertEquals(application.getApplicationId(), APPLICATION_ID);
        assertNotNull(application.getLastUpdated());
        verify(applicationRepository).save(application);
    }

    /**
     * Tests that a draft application should be updated
     */
    @Test
    public void shouldUpdateApplication() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication(getTemplate());
        draftApplication.setApplicationId(APPLICATION_ID);
        LocalDateTime now = LocalDateTime.now();
        draftApplication.setLastUpdated(now);

        Application created = applicationService.createApplication(draftApplication, true);

        assertEquals(draftApplication, created);
        assertTrue(created.getLastUpdated() != null && created.getLastUpdated().isAfter(now));
        verifyNoInteractions(templateRepository);
        verify(applicationRepository).save(draftApplication);
    }
    /**
     * Tests that an ApplicationException is thrown if an application is attempted to be updated without an ID
     */
    @Test
    public void shouldThrowExceptionOnUpdateNoId() {
        Application application = createDraftApplication(getTemplate());
        application.setId(null);

        assertThrows(ApplicationException.class, () -> applicationService.createApplication(application, true));

        verifyNoInteractions(templateRepository);
        verify(applicationRepository, times(0)).save(application);
    }

    /**
     * Tests that the application template should be retrieved successfully
     */
    @Test
    public void shouldGetApplicationTemplate() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication(getTemplate());
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
        DraftApplication draftApplication = (DraftApplication) createDraftApplication(getTemplate());
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

    /**
     * Tests that all application templates should be retrieved
     */
    @Test
    public void shouldGetApplicationTemplates() {
        given(loader.loadTemplates())
                .willReturn(templates);

        ApplicationTemplate[] returned = applicationService.getApplicationTemplates();

        assertEquals(templates, returned);
        verify(loader).loadTemplates();
    }

    /**
     * Tests that an application should be submitted successfully
     */
    @Test
    public void shouldSubmitApplication() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication(templates[0]);
        Application submitted = createSubmittedApplication(draftApplication);

        Application returned = applicationService.submitApplication(draftApplication);

        assertEquals(submitted, returned);
        assertTrue(returned instanceof SubmittedApplication);
        assertEquals(ApplicationStatus.SUBMITTED, returned.getStatus());
        assertNotNull(returned.getLastUpdated());
        assertEquals(draftApplication.getApplicationId(), returned.getApplicationId());
        verify(applicationRepository).delete(draftApplication);
        verify(applicationRepository).save(submitted);
    }

    /**
     * Tests that if an application is not in a draft/referred state when being submitted, an ApplicationException will be thrown
     */
    @Test
    public void shouldThrowIfIncorrectApplicationBeingSubmitted() {
        Application submitted = createSubmittedApplication((DraftApplication) createDraftApplication(templates[0]));

        assertThrows(ApplicationException.class, () -> applicationService.submitApplication(submitted));

        verifyNoInteractions(applicationRepository);
    }
}
