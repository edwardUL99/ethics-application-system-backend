package ie.ul.ethics.scieng.applications.controllers;

import ie.ul.ethics.scieng.applications.models.ApplicationTemplateResponse;
import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationResponse;
import ie.ul.ethics.scieng.applications.models.UpdateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.models.mapping.ApplicationRequestMapper;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.common.Constants;
import ie.ul.ethics.scieng.test.utils.JSON;

import static ie.ul.ethics.scieng.common.Constants.*;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.USERNAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ie.ul.ethics.scieng.users.models.User;
import ie.ul.ethics.scieng.users.services.UserService;
import ie.ul.ethics.scieng.applications.services.ApplicationServiceTest;
import ie.ul.ethics.scieng.applications.test.config.TestConfiguration;
import ie.ul.ethics.scieng.test.utils.TestApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * This class tests the ApplicationController
 */
@SpringBootTest(classes = {
        TestApplication.class,
        TestConfiguration.class
}, properties = {
        "auth.jwt.secret=ethics-secret-hashing-key-thirty-five-characters-long",
        "auth.jwt.token.validity=2"
})
public class ApplicationControllerTest {
    /**
     * The mock authentication information object
     */
    @MockBean
    private AuthenticationInformation authenticationInformation;
    /**
     * The mock application service
     */
    @MockBean
    private ApplicationService applicationService;
    /**
     * The mock user service
     */
    @MockBean
    private UserService userService;
    /**
     * The mock request mapper
     */
    @MockBean
    private ApplicationRequestMapper requestMapper;
    /**
     * The loaded templates
     */
    private final ApplicationTemplate[] templates;
    /**
     * Web app context used for testing
     */
    @Autowired
    private WebApplicationContext webApplicationContext;
    /**
     * Used for mocking interactions with the model view controller
     */
    private MockMvc mockMvc;

    /**
     * Create a test class instance
     */
    @Autowired
    public ApplicationControllerTest(ApplicationTemplateLoader templateLoader) {
        this.templates = templateLoader.loadTemplates();
    }

    /**
     * Initialises the test mocks
     */
    @BeforeEach
    private void initMocks() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.openMocks(this);
    }

    /**
     * This test tests that all the loaded templates should be loaded and retrieved
     */
    @Test
    public void shouldGetAllApplicationTemplates() throws Exception {
        given(applicationService.getApplicationTemplates())
                .willReturn(templates);

        ApplicationTemplateResponse response = new ApplicationTemplateResponse(templates);
        String json = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS, "templates")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(json));

        verify(applicationService).getApplicationTemplates();
    }

    /**
     * Tests that an application should be retrieved successfully
     */
    @Test
    public void shouldGetApplicationSuccessfully() throws Exception {
        Application draft = ApplicationServiceTest.createDraftApplication(templates[0]);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(applicationService.getApplication(ApplicationServiceTest.APPLICATION_DB_ID))
                .willReturn(draft);
        given(userService.loadUser(USERNAME))
                .willReturn(draft.getUser());

        String json = JSON.convertJSON(draft);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("id", "" + ApplicationServiceTest.APPLICATION_DB_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(json));

        verify(applicationService).getApplication(ApplicationServiceTest.APPLICATION_DB_ID);
        verify(userService).loadUser(USERNAME);
        verify(authenticationInformation).getUsername();
    }

    /**
     * Should get not found on get application if the ID doesn't exist
     */
    @Test
    public void shouldThrowNotFoundOnGetApplication() throws Exception {
        given(applicationService.getApplication(ApplicationServiceTest.APPLICATION_DB_ID))
                .willReturn(null);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("id", "" + ApplicationServiceTest.APPLICATION_DB_ID))
                .andExpect(status().isNotFound());

        verify(applicationService).getApplication(ApplicationServiceTest.APPLICATION_DB_ID);
    }

    /**
     * Tests that insufficient permissions should be thrown on get application if the user cannot view the application
     */
    @Test
    public void shouldThrowInsufficientPermissionsOnGetApplication() throws Exception {
        Application application = ApplicationServiceTest.createDraftApplication(templates[0]);
        User user = ApplicationServiceTest.createTestUser();
        Account account = user.getAccount();
        account.setUsername("not_me");
        user.setAccount(account);

        given(applicationService.getApplication(ApplicationServiceTest.APPLICATION_DB_ID))
                .willReturn(application);
        given(userService.loadUser("not_me"))
                .willReturn(user);
        given(authenticationInformation.getUsername())
                .willReturn("not_me");

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INSUFFICIENT_PERMISSIONS);

        String result = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("id", "" + ApplicationServiceTest.APPLICATION_DB_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(ApplicationServiceTest.APPLICATION_DB_ID);
        verify(userService).loadUser("not_me");
        verify(authenticationInformation).getUsername();
    }

    /**
     * Tests that a draft application should be created
     * TODO test normal createApplication also and create Postman tests
     */
    @Test
    public void shouldCreateDraftApplication() throws Exception {
        DraftApplication draftApplication = (DraftApplication) ApplicationServiceTest.createDraftApplication(templates[0]);
        templates[0].setDatabaseId(ApplicationServiceTest.TEMPLATE_DB_ID);

        CreateDraftApplicationResponse response = new CreateDraftApplicationResponse(draftApplication);
        CreateDraftApplicationRequest request =
                new CreateDraftApplicationRequest(draftApplication.getUser().getUsername(), templates[0], draftApplication.getAnswers());

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(requestMapper.createDraftRequestToDraft(request))
                .willReturn(draftApplication);
        given(applicationService.createApplication(draftApplication, false))
                .willReturn(draftApplication);

        mockMvc.perform(post(createApiPath(Constants.Endpoint.APPLICATIONS, "draft"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(applicationService).createApplication(draftApplication, false);
        verify(requestMapper).createDraftRequestToDraft(request);
    }

    /**
     * Tests that if a user attempts to edit another user's draft, insufficient permissions are thrown
     */
    @Test
    public void shouldThrowInsufficientPermissionsOnCreate() throws Exception {
        DraftApplication draftApplication = (DraftApplication) ApplicationServiceTest.createDraftApplication(templates[0]);
        templates[0].setDatabaseId(ApplicationServiceTest.TEMPLATE_DB_ID);

        CreateDraftApplicationRequest request =
                new CreateDraftApplicationRequest(draftApplication.getUser().getUsername(), templates[0], draftApplication.getAnswers());

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INSUFFICIENT_PERMISSIONS);
        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn("not_my_username");

        mockMvc.perform(post(createApiPath(Constants.Endpoint.APPLICATIONS, "draft"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verifyNoInteractions(applicationService);
        verifyNoInteractions(requestMapper);
    }

    /**
     * This tests that if a user is not found when mapping the request to create a draft, an error is thrown
     */
    @Test
    public void shouldThrowErrorIfUserNotFoundCreateDraft() throws Exception {
        DraftApplication draftApplication = (DraftApplication) ApplicationServiceTest.createDraftApplication(templates[0]);
        templates[0].setDatabaseId(ApplicationServiceTest.TEMPLATE_DB_ID);

        CreateDraftApplicationRequest request =
                new CreateDraftApplicationRequest(draftApplication.getUser().getUsername(), templates[0], new HashMap<>());

        draftApplication.setUser(null);

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, USER_NOT_FOUND);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(requestMapper.createDraftRequestToDraft(request))
                .willReturn(draftApplication);

        mockMvc.perform(post(createApiPath(Constants.Endpoint.APPLICATIONS, "draft"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(applicationService, times(0)).createApplication(draftApplication, false);
        verify(requestMapper).createDraftRequestToDraft(request);
    }

    /**
     * Tests that a draft application should be updated
     */
    @Test
    public void shouldUpdateDraftApplication() throws Exception {
        DraftApplication draftApplication = (DraftApplication) ApplicationServiceTest.createDraftApplication(templates[0]);

        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_DB_ID, new HashMap<>());
        Map<String, Object> response = new HashMap<>();
        response.put(MESSAGE, APPLICATION_UPDATED);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(requestMapper.updateDraftRequestToDraft(request))
                .willReturn(draftApplication);
        given(applicationService.createApplication(draftApplication, true))
                .willReturn(draftApplication);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "draft"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(requestMapper).updateDraftRequestToDraft(request);
        verify(applicationService).createApplication(draftApplication, true);
    }

    /**
     * Tests that if a user attempts to edit another user's draft, insufficient permissions are thrown
     */
    @Test
    public void shouldThrowInsufficientPermissionsOnUpdate() throws Exception {
        DraftApplication draftApplication = (DraftApplication) ApplicationServiceTest.createDraftApplication(templates[0]);

        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_DB_ID, new HashMap<>());
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INSUFFICIENT_PERMISSIONS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn("not_my_username");
        given(requestMapper.updateDraftRequestToDraft(request))
                .willReturn(draftApplication);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "draft"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(requestMapper).updateDraftRequestToDraft(request);
        verifyNoInteractions(applicationService);
    }

    /**
     * Tests that an error should be thrown if the application is not a draft that is being updated
     */
    @Test
    public void shouldThrowErrorIfApplicationNotDraft() throws Exception {
        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_DB_ID, new HashMap<>());
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, APPLICATION_NOT_DRAFT);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        doThrow(IllegalStateException.class).when(requestMapper).updateDraftRequestToDraft(request);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "draft"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).updateDraftRequestToDraft(request);
        verifyNoInteractions(applicationService);
    }

    /**
     * Should throw not found if application doesn't exist
     */
    @Test
    public void shouldThrowNotFoundOnUpdate() throws Exception {
        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_DB_ID, new HashMap<>());

        String json = JSON.convertJSON(request);

        given(requestMapper.updateDraftRequestToDraft(request))
                .willReturn(null);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "draft"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(requestMapper).updateDraftRequestToDraft(request);
        verifyNoInteractions(applicationService);
    }
}
