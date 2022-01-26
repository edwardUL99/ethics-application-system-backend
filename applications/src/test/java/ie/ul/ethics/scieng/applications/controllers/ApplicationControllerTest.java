package ie.ul.ethics.scieng.applications.controllers;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.exceptions.MappingException;
import ie.ul.ethics.scieng.applications.models.*;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.models.applications.ReferredApplication;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
import ie.ul.ethics.scieng.applications.models.applications.ids.ApplicationIDPolicy;
import ie.ul.ethics.scieng.applications.models.mapping.AcceptResubmittedRequest;
import ie.ul.ethics.scieng.applications.models.mapping.ApplicationRequestMapper;
import ie.ul.ethics.scieng.applications.models.mapping.MappedAcceptResubmittedRequest;
import ie.ul.ethics.scieng.applications.models.mapping.MappedReferApplicationRequest;
import ie.ul.ethics.scieng.applications.models.mapping.ReviewApplicationRequest;
import ie.ul.ethics.scieng.applications.services.ApplicationService;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplate;
import ie.ul.ethics.scieng.applications.templates.ApplicationTemplateLoader;

import ie.ul.ethics.scieng.authentication.jwt.AuthenticationInformation;
import ie.ul.ethics.scieng.common.Constants;
import ie.ul.ethics.scieng.test.utils.JSON;

import static ie.ul.ethics.scieng.applications.services.ApplicationServiceTest.*;
import static ie.ul.ethics.scieng.common.Constants.*;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.USERNAME;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ie.ul.ethics.scieng.users.authorization.Roles;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * A username for a referrer account
     */
    private static final String REFERRER_USERNAME = "referrer";

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
        user.setUsername("not_me");

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
        user.setUsername("not_me");

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
     * Tests that the list of assigned applications should be retrieved successfully
     */
    @Test
    public void shouldGetAssignedApplicationsSuccessfully() throws Exception {
        Application submittedApplication = createSubmittedApplication((DraftApplication) createDraftApplication(templates[0]));
        User user = createTestUser();
        user.setUsername("committee");
        user.setRole(Roles.COMMITTEE_MEMBER);

        ApplicationResponse response = ApplicationResponseFactory.buildResponse(submittedApplication);
        List<ApplicationResponse> applications = new ArrayList<>();
        applications.add(response);

        String result = JSON.convertJSON(applications);

        given(authenticationInformation.getUsername())
                .willReturn("committee");
        given(userService.loadUser("committee"))
                .willReturn(user);
        given(applicationService.getAssignedApplications(user))
                .willReturn(List.of(submittedApplication));

        mockMvc.perform(get(createApiPath(Endpoint.APPLICATIONS, "user"))
                .param("viewable", "false"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(userService).loadUser("committee");
        verify(applicationService).getAssignedApplications(user);
    }

    /**
     * Tests that the list of viewable applications should be retrieved successfully
     */
    @Test
    public void shouldGetViewableApplicationsSuccessfully() throws Exception {
        Application draft = createDraftApplication(templates[0]);

        ApplicationResponse response = ApplicationResponseFactory.buildResponse(draft);
        List<ApplicationResponse> applications = new ArrayList<>();
        applications.add(response);
        User user = draft.getUser();

        String result = JSON.convertJSON(applications);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(userService.loadUser(USERNAME))
                .willReturn(user);
        given(applicationService.getViewableApplications(user))
                .willReturn(List.of(draft));

        mockMvc.perform(get(createApiPath(Endpoint.APPLICATIONS, "user"))
                        .param("viewable", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(userService).loadUser(USERNAME);
        verify(applicationService).getViewableApplications(user);
    }

    /**
     * Tests that a 404 error is thrown if the user does not exist
     */
    @Test
    public void shouldThrowNotFoundOnGetUserApplications() throws Exception {
        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(userService.loadUser(USERNAME))
                .willReturn(null);

        mockMvc.perform(get(createApiPath(Endpoint.APPLICATIONS, "user"))
                        .param("viewable", "true"))
                .andExpect(status().isNotFound());

        verify(authenticationInformation).getUsername();
        verify(userService).loadUser(USERNAME);
        verifyNoInteractions(applicationService);
    }

    /**
     * Tests that an insufficient permissions should be thrown if the user that attempts to retrieve assigned applications
     * is not a committee member
     */
    @Test
    public void shouldThrowInsufficientPermissionsOnGetAssignedApplications() throws Exception {
        Application submittedApplication = createSubmittedApplication((DraftApplication) createDraftApplication(templates[0]));
        User user = submittedApplication.getUser();

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INSUFFICIENT_PERMISSIONS);

        String result = JSON.convertJSON(response);

        given(authenticationInformation.getUsername())
                .willReturn(USERNAME);
        given(userService.loadUser(USERNAME))
                .willReturn(user);
        doThrow(ApplicationException.class).when(applicationService).getAssignedApplications(user);

        mockMvc.perform(get(createApiPath(Endpoint.APPLICATIONS, "user"))
                        .param("viewable", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(authenticationInformation).getUsername();
        verify(userService).loadUser(USERNAME);
        verify(applicationService).getAssignedApplications(user);
    }

    /**
     * Tests that a draft application should be created
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

    /**
     * Create a test resubmitted application
     * @return the test application instance
     */
    private Application createResubmitted() {
        DraftApplication draftApplication = (DraftApplication) createDraftApplication(templates[0]);
        SubmittedApplication submitted = (SubmittedApplication) createSubmittedApplication(draftApplication);
        User referrer = createTestUser();
        referrer.setUsername("referrer");
        referrer.setRole(Roles.CHAIR);
        submitted.assignCommitteeMember(referrer);
        submitted.setStatus(ApplicationStatus.RESUBMITTED);
        submitted.assignCommitteeMembersToPrevious();

        return submitted;
    }

    /**
     * Tests that a resubmitted application should be accepted
     */
    @Test
    public void shouldAcceptResubmittedApplication() throws Exception {
        SubmittedApplication resubmitted = (SubmittedApplication) createResubmitted();
        SubmittedApplication submitted = (SubmittedApplication) createResubmitted();
        submitted.setStatus(ApplicationStatus.REVIEW);
        resubmitted.getPreviousCommitteeMembers().forEach(submitted::assignCommitteeMember);
        submitted.clearPreviousCommitteeMembers();

        AcceptResubmittedRequest request = new AcceptResubmittedRequest(APPLICATION_ID, List.of(USERNAME));
        ApplicationResponse response = ApplicationResponseFactory.buildResponse(submitted);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        List<User> committeeMembers = List.of(submitted.getUser());

        given(requestMapper.mapAcceptResubmittedRequest(request))
                .willReturn(new MappedAcceptResubmittedRequest(resubmitted, committeeMembers));
        given(applicationService.acceptResubmitted(resubmitted, committeeMembers))
                .willReturn(submitted);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "resubmit"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).mapAcceptResubmittedRequest(request);
        verify(applicationService).acceptResubmitted(resubmitted, List.of(submitted.getUser()));
    }

    /**
     * Tests that an Illegal status error is thrown on accept resubmit if the status is wrong
     */
    @Test
    public void shouldThrowIllegalStatusErrorOnAcceptResubmit() throws Exception {
        SubmittedApplication submitted = (SubmittedApplication) createResubmitted();
        submitted.setStatus(ApplicationStatus.SUBMITTED);

        AcceptResubmittedRequest request = new AcceptResubmittedRequest(APPLICATION_ID, List.of(USERNAME));
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_APPLICATION_STATUS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        List<User> committeeMembers = List.of(submitted.getUser());

        given(requestMapper.mapAcceptResubmittedRequest(request))
                .willReturn(new MappedAcceptResubmittedRequest(submitted, committeeMembers));
        doThrow(InvalidStatusException.class).when(applicationService).acceptResubmitted(submitted, committeeMembers);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "resubmit"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).mapAcceptResubmittedRequest(request);
        verify(applicationService).acceptResubmitted(submitted, List.of(submitted.getUser()));
    }

    /**
     * Tests that an ApplicationException is thrown on accept resubmit if something goes wrong
     */
    @Test
    public void shouldThrowApplicationExceptionOnAcceptResubmit() throws Exception {
        SubmittedApplication submitted = (SubmittedApplication) createResubmitted();

        AcceptResubmittedRequest request = new AcceptResubmittedRequest(APPLICATION_ID, List.of(USERNAME));
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, null);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        List<User> committeeMembers = List.of(submitted.getUser());

        given(requestMapper.mapAcceptResubmittedRequest(request))
                .willReturn(new MappedAcceptResubmittedRequest(submitted, committeeMembers));
        doThrow(ApplicationException.class).when(applicationService).acceptResubmitted(submitted, committeeMembers);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "resubmit"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).mapAcceptResubmittedRequest(request);
        verify(applicationService).acceptResubmitted(submitted, List.of(submitted.getUser()));
    }

    /**
     * Tests that a 404 error should be thrown if either the application or a committee member doesn't exist
     */
    @Test
    public void shouldThrowNotFoundOnAcceptResubmitted() throws Exception {
        SubmittedApplication submitted = (SubmittedApplication) createResubmitted();

        AcceptResubmittedRequest request = new AcceptResubmittedRequest(APPLICATION_ID, List.of(USERNAME));
        String json = JSON.convertJSON(request);
        List<User> committeeMembers = List.of(submitted.getUser());

        given(requestMapper.mapAcceptResubmittedRequest(request))
                .willReturn(new MappedAcceptResubmittedRequest(null, committeeMembers));

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "resubmit"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(requestMapper).mapAcceptResubmittedRequest(request);

        request = new AcceptResubmittedRequest(APPLICATION_ID, List.of(USERNAME, "not_found"));
        json = JSON.convertJSON(request);

        committeeMembers = new ArrayList<>();
        committeeMembers.add(submitted.getUser());
        committeeMembers.add(null);

        given(requestMapper.mapAcceptResubmittedRequest(request))
                .willReturn(new MappedAcceptResubmittedRequest(submitted, committeeMembers));

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "resubmit"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(requestMapper).mapAcceptResubmittedRequest(request);
        verifyNoInteractions(applicationService);
    }

    /**
     * Tests that an application should be set to review correctly
     */
    @Test
    public void shouldReviewApplication() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application submitted = createSubmittedApplication(draft);
        Application review = createSubmittedApplication(draft);
        review.setStatus(ApplicationStatus.REVIEW);

        ReviewApplicationRequest request = new ReviewApplicationRequest(APPLICATION_ID, false);
        ApplicationResponse response = ApplicationResponseFactory.buildResponse(review);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(submitted);
        given(applicationService.reviewApplication(submitted, false))
                .willReturn(review);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "review"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(applicationService).reviewApplication(submitted, false);
    }

    /**
     * Tests that an application should be set to reviewed
     */
    @Test
    public void shouldSetApplicationToReviewed() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application review = createSubmittedApplication(draft);
        review.setStatus(ApplicationStatus.REVIEW);
        Application reviewed = createSubmittedApplication(draft);
        reviewed.setStatus(ApplicationStatus.REVIEWED);

        ReviewApplicationRequest request = new ReviewApplicationRequest(APPLICATION_ID, true);
        ApplicationResponse response = ApplicationResponseFactory.buildResponse(reviewed);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(review);
        given(applicationService.reviewApplication(review, true))
                .willReturn(reviewed);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "review"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(applicationService).reviewApplication(review, true);
    }

    /**
     * Tests that a 404 should be returned if the application in the review application request does not exist
     */
    @Test
    public void shouldThrowNotFoundOnReview() throws Exception {
        ReviewApplicationRequest request = new ReviewApplicationRequest(APPLICATION_ID, false);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(null);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "review"))
                .contentType(JSON.MEDIA_TYPE)
                .content(JSON.convertJSON(request)))
                .andExpect(status().isNotFound());

        verify(applicationService).getApplication(APPLICATION_ID);
        verifyNoMoreInteractions(applicationService);
    }

    /**
     * Tests than an Invalid application status should be returned if the application status is incorrect
     */
    @Test
    public void shouldThrowInvalidStatusOnReview() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application application = createSubmittedApplication(draft);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(application);
        doThrow(InvalidStatusException.class).when(applicationService).reviewApplication(application, true);

        ReviewApplicationRequest request = new ReviewApplicationRequest(APPLICATION_ID, true);
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_APPLICATION_STATUS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "review"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(applicationService).reviewApplication(application, true);
    }

    /**
     * Tests that comments are successfully added to an application
     */
    @Test
    public void shouldAddCommentsToApplication() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application review = createSubmittedApplication(draft);
        review.setStatus(ApplicationStatus.REVIEW);
        SubmittedApplication mapped = (SubmittedApplication) createSubmittedApplication(draft);
        mapped.setStatus(ApplicationStatus.REVIEW);

        Comment comment = new Comment(null, null, "comment", "component", new ArrayList<>());
        comment.addSubComment(new Comment(null, null, "comment1", "component1", new ArrayList<>()));
        mapped.addComment(comment);

        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>());
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>()));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(ApplicationResponseFactory.buildResponse(mapped));

        given(requestMapper.reviewSubmittedRequestToSubmitted(request))
                .willReturn(mapped);
        given(applicationService.createApplication(mapped, true))
                .willReturn(mapped);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "review"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).reviewSubmittedRequestToSubmitted(request);
        verify(applicationService).createApplication(mapped, true);
    }

    /**
     * Tests that if any comment is null when mapping a review request, a USER_NOT_FOUND error is thrown
     */
    @Test
    public void shouldThrowUserNullOnAddCommentsToApplication() throws Exception {
        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>());
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>()));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, USER_NOT_FOUND);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        doThrow(MappingException.class).when(requestMapper).reviewSubmittedRequestToSubmitted(request);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "review"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).reviewSubmittedRequestToSubmitted(request);
        verifyNoInteractions(applicationService);
    }

    /**
     * Tests that if application status is wrong when mapping a review request, an INVALID_APPLICATION_STATUS error is thrown
     */
    @Test
    public void shouldThrowInvalidStatusOnAddCommentsToApplication() throws Exception {
        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>());
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>()));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_APPLICATION_STATUS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        doThrow(InvalidStatusException.class).when(requestMapper).reviewSubmittedRequestToSubmitted(request);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "review"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).reviewSubmittedRequestToSubmitted(request);
        verifyNoInteractions(applicationService);
    }

    /**
     * Tests that if an application is not found on adding comments to it, it should throw 404
     */
    @Test
    public void shouldThrowNotFoundOnAddCommentsToApplication() throws Exception {
        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>());
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>()));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        String json = JSON.convertJSON(request);

        given(requestMapper.reviewSubmittedRequestToSubmitted(request))
                .willReturn(null);

        mockMvc.perform(put(createApiPath(Endpoint.APPLICATIONS, "review"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(requestMapper).reviewSubmittedRequestToSubmitted(request);
        verifyNoInteractions(applicationService);
    }

    /**
     * Tests that an application should be approved successfully
     */
    @Test
    public void shouldApproveApplication() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application reviewed = createSubmittedApplication(draft);
        reviewed.setStatus(ApplicationStatus.REVIEWED);
        Application approved = createSubmittedApplication(draft);
        approved.setStatus(ApplicationStatus.APPROVED);
        Comment finalComment = new Comment();
        ((SubmittedApplication)approved).setFinalComment(finalComment);

        ApproveApplicationRequest request = new ApproveApplicationRequest(APPLICATION_ID, true, finalComment);
        ApplicationResponse response = ApplicationResponseFactory.buildResponse(approved);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(reviewed);
        given(applicationService.approveApplication(reviewed, true, finalComment))
                .willReturn(approved);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "approve"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(applicationService).approveApplication(reviewed, true, finalComment);
    }

    /**
     * Tests that an application should be rejected successfully
     */
    @Test
    public void shouldRejectApplication() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application reviewed = createSubmittedApplication(draft);
        reviewed.setStatus(ApplicationStatus.REVIEWED);
        Application rejected = createSubmittedApplication(draft);
        rejected.setStatus(ApplicationStatus.APPROVED);
        Comment finalComment = new Comment();
        ((SubmittedApplication)rejected).setFinalComment(finalComment);

        ApproveApplicationRequest request = new ApproveApplicationRequest(APPLICATION_ID, false, finalComment);
        ApplicationResponse response = ApplicationResponseFactory.buildResponse(rejected);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(reviewed);
        given(applicationService.approveApplication(reviewed, false, finalComment))
                .willReturn(rejected);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "approve"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(applicationService).approveApplication(reviewed, false, finalComment);
    }

    /**
     * Tests that a 404 error should be thrown if the application doesn't exist
     */
    @Test
    public void shouldThrowIfApplicationNotFoundOnApprove() throws Exception {
        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(null);

        Comment finalComment = new Comment();

        ApproveApplicationRequest request = new ApproveApplicationRequest(APPLICATION_ID, true, finalComment);

        String json = JSON.convertJSON(request);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "approve"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(applicationService).getApplication(APPLICATION_ID);
        verifyNoMoreInteractions(applicationService);
    }

    /**
     * Tests that an invalid application status error is thrown if the application is in the wrong status
     */
    @Test
    public void shouldThrowInvalidStatusErrorOnApproveApplication() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application submitted = createSubmittedApplication(draft);
        Comment finalComment = new Comment();

        ApproveApplicationRequest request = new ApproveApplicationRequest(APPLICATION_ID, true, finalComment);
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_APPLICATION_STATUS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(submitted);
        doThrow(InvalidStatusException.class).when(applicationService).approveApplication(submitted, true, finalComment);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "approve"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(applicationService).approveApplication(submitted, true, finalComment);
    }

    /**
     * Create a test ReferredApplication instance
     * @param base the base application to refer
     * @return the referred application instance
     */
    private Application createReferredApplication(Application base) {
        User referrer = createTestUser();
        referrer.getAccount().setUsername(REFERRER_USERNAME);
        referrer.setRole(Roles.CHAIR);

        return new ReferredApplication(APPLICATION_DB_ID, APPLICATION_ID, createTestUser(),
                templates[0], base.getAnswers(), new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>(), referrer);
    }

    /**
     * Tests that an application should be referred to the applicant successfully
     */
    @Test
    public void shouldReferApplication() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application reviewed = createSubmittedApplication(draft);
        reviewed.setStatus(ApplicationStatus.REVIEWED);
        ReferredApplication referred = (ReferredApplication) createReferredApplication(reviewed);
        User referrer = referred.getReferredBy();

        ReferApplicationRequest request = new ReferApplicationRequest(APPLICATION_ID, new ArrayList<>(), REFERRER_USERNAME);
        ApplicationResponse response = ApplicationResponseFactory.buildResponse(referred);
        MappedReferApplicationRequest mapped = new MappedReferApplicationRequest(reviewed, new ArrayList<>(), referrer);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(requestMapper.mapReferApplicationRequest(request))
                .willReturn(mapped);
        given(applicationService.referApplication(reviewed, new ArrayList<>(), referrer))
                .willReturn(referred);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "refer"))
                .contentType(JSON.MEDIA_TYPE)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).mapReferApplicationRequest(request);
        verify(applicationService).referApplication(reviewed, new ArrayList<>(), referrer);
    }

    /**
     * Tests that an invalid status error should be thrown when an application is referred
     */
    @Test
    public void shouldThrowInvalidStatusOnRefer() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application reviewed = createSubmittedApplication(draft);
        User referrer = createTestUser();
        referrer.getAccount().setUsername(REFERRER_USERNAME);
        referrer.setRole(Roles.CHAIR);

        ReferApplicationRequest request = new ReferApplicationRequest(APPLICATION_ID, new ArrayList<>(), REFERRER_USERNAME);
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, INVALID_APPLICATION_STATUS);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(requestMapper.mapReferApplicationRequest(request))
                .willReturn(new MappedReferApplicationRequest(reviewed, new ArrayList<>(), referrer));
        doThrow(InvalidStatusException.class).when(applicationService).referApplication(reviewed, new ArrayList<>(), referrer);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "refer"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).mapReferApplicationRequest(request);
        verify(applicationService).referApplication(reviewed, new ArrayList<>(), referrer);
    }

    /**
     * Tests that a general application error should be thrown when an application is referred
     */
    @Test
    public void shouldThrowApplicationExceptionOnRefer() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application reviewed = createSubmittedApplication(draft);
        User referrer = createTestUser();
        referrer.getAccount().setUsername(REFERRER_USERNAME);
        referrer.setRole(Roles.CHAIR);

        ReferApplicationRequest request = new ReferApplicationRequest(APPLICATION_ID, new ArrayList<>(), REFERRER_USERNAME);
        Map<String, Object> response = new HashMap<>();
        response.put(ERROR, null);

        String json = JSON.convertJSON(request);
        String result = JSON.convertJSON(response);

        given(requestMapper.mapReferApplicationRequest(request))
                .willReturn(new MappedReferApplicationRequest(reviewed, new ArrayList<>(), referrer));
        doThrow(ApplicationException.class).when(applicationService).referApplication(reviewed, new ArrayList<>(), referrer);

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "refer"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(JSON.MEDIA_TYPE))
                .andExpect(content().json(result));

        verify(requestMapper).mapReferApplicationRequest(request);
        verify(applicationService).referApplication(reviewed, new ArrayList<>(), referrer);
    }

    /**
     * Tests that a 404 should be thrown if either the application or referring user is not found
     */
    @Test
    public void shouldThrowNotFoundOnReferApplication() throws Exception {
        DraftApplication draft = (DraftApplication) createDraftApplication(templates[0]);
        Application reviewed = createSubmittedApplication(draft);
        User referrer = createTestUser();
        referrer.getAccount().setUsername(REFERRER_USERNAME);
        referrer.setRole(Roles.CHAIR);

        ReferApplicationRequest request = new ReferApplicationRequest(APPLICATION_ID, new ArrayList<>(), REFERRER_USERNAME);

        String json = JSON.convertJSON(request);

        given(requestMapper.mapReferApplicationRequest(request))
                .willReturn(new MappedReferApplicationRequest(null, new ArrayList<>(), referrer));

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "refer"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        given(requestMapper.mapReferApplicationRequest(request))
                .willReturn(new MappedReferApplicationRequest(reviewed, new ArrayList<>(), null));

        mockMvc.perform(post(createApiPath(Endpoint.APPLICATIONS, "refer"))
                        .contentType(JSON.MEDIA_TYPE)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(requestMapper, times(2)).mapReferApplicationRequest(request);
        verify(applicationService, times(0)).referApplication(reviewed, new ArrayList<>(), referrer);
    }
}
