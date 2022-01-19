package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.UpdateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;
import ie.ul.ethics.scieng.authentication.jwt.JWT;
import ie.ul.ethics.scieng.authentication.jwt.JwtRequestFilter;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.users.authorization.Roles;
import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import ie.ul.ethics.scieng.applications.templates.config.TemplatesConfiguration;
import ie.ul.ethics.scieng.applications.test.config.TestConfiguration;
import ie.ul.ethics.scieng.test.utils.TestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashMap;
import java.util.Map;

import static ie.ul.ethics.scieng.test.utils.constants.Users.*;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * This class tests the ApplicationRequestMapper interface
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
public class ApplicationRequestMapperTest {
    /**
     * The mocked user service
     */
    @MockBean
    private UserService userService;
    /**
     * The mocked application service
     */
    @MockBean
    private ApplicationService applicationService;
    /**
     * The request mapper under test
     */
    @Autowired
    private ApplicationRequestMapper requestMapper;
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
    public ApplicationRequestMapperTest(ApplicationTemplateLoader templateLoader) {
        this.templates = templateLoader.loadTemplates();
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
        HashMap<String, Answer> values = new HashMap<>();
        String[] components = {"component1", "component2", "component3", "component4"};
        String[] answers = {"answer1", "answer2", "answer3", "answer4"};

        for (int i = 0; i < components.length; i++) {
            String id = components[i];
            values.put(id, new Answer(null, id, answers[i], Answer.ValueType.TEXT));
        }

        return new DraftApplication(APPLICATION_DB_ID, APPLICATION_ID, createTestUser(), getTemplate(), values);
    }

    /**
     * Tests that a CreateDraftRequest should be mapped correctly
     */
    @Test
    public void shouldMapCreateDraftRequest() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication();
        draftApplication.setId(null);
        draftApplication.setApplicationId(null);
        CreateDraftApplicationRequest request =
                new CreateDraftApplicationRequest(USERNAME, draftApplication.getApplicationTemplate(), draftApplication.getAnswers());

        given(userService.loadUser(USERNAME))
                .willReturn(draftApplication.getUser());

        DraftApplication returned = requestMapper.createDraftRequestToDraft(request);

        assertEquals(draftApplication, returned);
        verify(userService).loadUser(USERNAME);
    }

    /**
     * Tests that an UpdateDraftRequest should be mapped correctly
     */
    @Test
    public void shouldMapUpdateDraftRequest() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication();
        ApplicationTemplate template = draftApplication.getApplicationTemplate();
        template.setDatabaseId(TEMPLATE_DB_ID);

        Map<String, Answer> oldValues = draftApplication.getAnswers();
        Map<String, Answer> newValues = new HashMap<>(oldValues);
        newValues.put("component5", new Answer(null, "component5", "answer5", Answer.ValueType.TEXT));

        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_DB_ID, newValues);

        assertNotEquals(oldValues, newValues);

        given(applicationService.getApplication(APPLICATION_DB_ID))
                .willReturn(draftApplication);

        DraftApplication returned = requestMapper.updateDraftRequestToDraft(request);

        assertEquals(draftApplication, returned);
        assertEquals(draftApplication.getAnswers(), newValues);
        verify(applicationService).getApplication(APPLICATION_DB_ID);
    }

    /**
     * A "hack" to set not draft status on draft application TODO remove this when other application types are implemented
     * @return the application that is a draft but different status
     */
    private Application getStatusChangeableDraftApplication() {
        DraftApplication temp = (DraftApplication) createDraftApplication();

        return new DraftApplication(temp.getId(), temp.getApplicationId(),
                temp.getUser(), temp.getApplicationTemplate(), temp.getAnswers()) {
            @Override
            public void setStatus(ApplicationStatus status) {
                this.status = status;;
            }
        };
    }

    /**
     * Tests that an IllegalStateException should be thrown if the given application ID is not a draft application
     */
    @Test
    public void shouldThrowIllegalStateIfIDNotDraft() {
        Application changeable = getStatusChangeableDraftApplication();
        changeable.setStatus(ApplicationStatus.SUBMITTED);

        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_DB_ID, new HashMap<>());

        given(applicationService.getApplication(APPLICATION_DB_ID))
                .willReturn(changeable);

        assertThrows(IllegalStateException.class, () -> requestMapper.updateDraftRequestToDraft(request));

        verify(applicationService).getApplication(APPLICATION_DB_ID);
    }

    /**
     * Tests that the mapper should return null if the application ID doesn't exist
     */
    @Test
    public void shouldReturnNullIfDraftIdNotExists() {
        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_DB_ID, new HashMap<>());

        given(applicationService.getApplication(APPLICATION_DB_ID))
                .willReturn(null);

        assertNull(requestMapper.updateDraftRequestToDraft(request));

        verify(applicationService).getApplication(APPLICATION_DB_ID);
    }
}