package ie.ul.ethics.scieng.applications.controllers;

import ie.ul.ethics.scieng.applications.exceptions.MappingException;
import ie.ul.ethics.scieng.applications.models.ApplicationResponse;
import ie.ul.ethics.scieng.applications.models.ApplicationResponseFactory;
import ie.ul.ethics.scieng.applications.models.ApplicationTemplateResponse;
import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationResponse;
import ie.ul.ethics.scieng.applications.models.SubmitApplicationRequest;
import ie.ul.ethics.scieng.applications.models.UpdateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.models.applications.ids.ApplicationIDPolicy;
import ie.ul.ethics.scieng.applications.models.mapping.ApplicationRequestMapper;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.authentication.models.Account;
import ie.ul.ethics.scieng.common.Constants;
import ie.ul.ethics.scieng.test.utils.JSON;

import static ie.ul.ethics.scieng.applications.services.ApplicationServiceTest.*;
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
     * The mock bean for generating IDs
     */
    @MockBean
    private ApplicationIDPolicy applicationIDPolicy;
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
     * Tests that an ID should be generated successfully
     */
    @Test
    public void shouldGenerateIDSuccessfully() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("id", APPLICATION_ID);
        String result = JSON.convertJSON(response);

        given(applicationIDPolicy.generate())
                .willReturn(APPLICATION_ID);

        mockMvc.perform(get(createApiPath(Endpoint.APPLICATIONS, "id")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationIDPolicy).generate();
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

        ApplicationResponse response = ApplicationResponseFactory.buildResponse(draft);
        String json = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("dbId", "" + ApplicationServiceTest.APPLICATION_DB_ID))
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
                        .param("dbId", "" + ApplicationServiceTest.APPLICATION_DB_ID))
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
                        .param("dbId", "" + ApplicationServiceTest.APPLICATION_DB_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(ApplicationServiceTest.APPLICATION_DB_ID);
        verify(userService).loadUser("not_me");
        verify(authenticationInformation).getUsername();
    }

    /**
     * Tests that an application should be retrieved successfully
     */
    @Test
    public void shouldGetApplicationSuccessfullyByAppId() throws Exception {
        Application draft = ApplicationServiceTest.createDraftApplication(templates[0]);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(draft);
        given(userService.loadUser(USERNAME))
                .willReturn(draft.getUser());

        ApplicationResponse response = ApplicationResponseFactory.buildResponse(draft);
        String json = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("id", APPLICATION_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(json));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(userService).loadUser(USERNAME);
        verify(authenticationInformation).getUsername();
    }

    /**
     * Should get not found on get application if the ID doesn't exist
     */
    @Test
    public void shouldThrowNotFoundOnGetApplicationByAppId() throws Exception {
        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(null);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("id", APPLICATION_ID))
                .andExpect(status().isNotFound());

        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that insufficient permissions should be thrown on get application if the user cannot view the application
     */
    @Test
    public void shouldThrowInsufficientPermissionsOnGetApplicationByAppId() throws Exception {
        Application application = ApplicationServiceTest.createDraftApplication(templates[0]);
        User user = ApplicationServiceTest.createTestUser();
        Account account = user.getAccount();
        account.setUsername("not_me");
        user.setAccount(account);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(application);
        given(userService.loadUser("not_me"))
                .willReturn(user);
        given(authenticationInformation.getUsername())
                .willReturn("not_me");

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INSUFFICIENT_PERMISSIONS);

        String result = JSON.convertJSON(response);

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("id", APPLICATION_ID))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(userService).loadUser("not_me");
        verify(authenticationInformation).getUsername();
    }

    /**
     * This method tests that if either id or applicationId are both provided or neither are provided, a bad request is thrown
     */
    @Test
    public void shouldThrowIfIllegalIDCombinationGivenOnGetApplication() throws Exception {
        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS))
                        .param("dbId", "" + ApplicationServiceTest.APPLICATION_DB_ID)
                        .param("id", APPLICATION_ID))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(createApiPath(Constants.Endpoint.APPLICATIONS)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(applicationService);
        verifyNoInteractions(userService);
        verifyNoInteractions(authenticationInformation);
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
                new CreateDraftApplicationRequest(draftApplication.getUser().getUsername(), templates[0], APPLICATION_ID, draftApplication.getAnswers());

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
        verify(applicationService).getApplication(APPLICATION_ID);
        verify(applicationService).createApplication(draftApplication, false);
        verify(requestMapper).createDraftRequestToDraft(request);
    }

    /**
     * Tests that a draft application should not be created if it already exists
     */
    @Test
    public void shouldNotCreateDraftApplicationIfAlreadyExists() throws Exception {
        DraftApplication draftApplication = (DraftApplication) ApplicationServiceTest.createDraftApplication(templates[0]);
        templates[0].setDatabaseId(ApplicationServiceTest.TEMPLATE_DB_ID);

        CreateDraftApplicationRequest request =
                new CreateDraftApplicationRequest(draftApplication.getUser().getUsername(), templates[0], APPLICATION_ID, draftApplication.getAnswers());

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, APPLICATION_ALREADY_EXISTS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(requestMapper.createDraftRequestToDraft(request))
                .willReturn(draftApplication);
        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(draftApplication);

        mockMvc.perform(post(createApiPath(Constants.Endpoint.APPLICATIONS, "draft"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(applicationService).getApplication(APPLICATION_ID);
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
                new CreateDraftApplicationRequest(draftApplication.getUser().getUsername(), templates[0], APPLICATION_ID, draftApplication.getAnswers());

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
                new CreateDraftApplicationRequest(draftApplication.getUser().getUsername(), templates[0], APPLICATION_ID, new HashMap<>());

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

        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_ID, new HashMap<>());
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

        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_ID, new HashMap<>());
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
        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_ID, new HashMap<>());
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_APPLICATION_STATUS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        doThrow(MappingException.class).when(requestMapper).updateDraftRequestToDraft(request);

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
        UpdateDraftApplicationRequest request = new UpdateDraftApplicationRequest(ApplicationServiceTest.APPLICATION_ID, new HashMap<>());

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

    /**
     * This method tests that a draft application should be s
     */
    @Test
    public void shouldSubmitApplication() throws Exception {
        Application draft = createDraftApplication(templates[0]);

        SubmitApplicationRequest request = new SubmitApplicationRequest(APPLICATION_ID);

        String json = JSON.convertJSON(request);
        Application submitted = createSubmittedApplication((DraftApplication) draft);
        String response = JSON.convertJSON(ApplicationResponseFactory.buildResponse(submitted));

        given(requestMapper.submitRequestToApplication(request))
                .willReturn(draft);
        given(applicationService.submitApplication(draft))
                .willReturn(submitted);
        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "submit"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(response));

        verify(requestMapper).submitRequestToApplication(request);
        verify(applicationService).submitApplication(draft);
        verify(authenticationInformation).getUsername();
    }

    /**
     * Tests that bad request is thrown if mapping fails
     */
    @Test
    public void shouldThrowBadRequestIfMappingFails() throws Exception {
        SubmitApplicationRequest request = new SubmitApplicationRequest(APPLICATION_ID);

        String json = JSON.convertJSON(request);

        doThrow(MappingException.class).when(requestMapper).submitRequestToApplication(request);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "submit"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest());

        verify(requestMapper).submitRequestToApplication(request);
        verifyNoInteractions(applicationService);
    }

    /**
     * Tests that insufficient permissions should be thrown if the application being submitted is not the user's own application
     */
    @Test
    public void shouldThrowInsufficientPermissionsIfNotOwnApplication() throws Exception {
        Application draft = createDraftApplication(templates[0]);

        SubmitApplicationRequest request = new SubmitApplicationRequest(APPLICATION_ID);

        String json = JSON.convertJSON(request);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(ERROR, INSUFFICIENT_PERMISSIONS);

        String response = JSON.convertJSON(responseMap);

        given(requestMapper.submitRequestToApplication(request))
                .willReturn(draft);
        given(authenticationInformation.getUsername())
                .willReturn("not_my_username");

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "submit"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(response));

        verify(requestMapper).submitRequestToApplication(request);
        verifyNoInteractions(applicationService);
        verify(authenticationInformation).getUsername();
    }

    /**
     * Tests that if an application is not found, a 404 is returned
     */
    @Test
    public void shouldThrowNotFoundIfApplicationDoesNotExist() throws Exception {
        SubmitApplicationRequest request = new SubmitApplicationRequest(APPLICATION_ID);

        String json = JSON.convertJSON(request);

        given(requestMapper.submitRequestToApplication(request))
                .willReturn(null);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "submit"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(requestMapper).submitRequestToApplication(request);
        verifyNoInteractions(applicationService);
    }
}
