package ie.ul.ethics.scieng.applications.models.mapping;

import ie.ul.ethics.scieng.applications.exceptions.ApplicationException;
import ie.ul.ethics.scieng.applications.exceptions.InvalidStatusException;
import ie.ul.ethics.scieng.applications.exceptions.MappingException;
import ie.ul.ethics.scieng.applications.models.CreateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.ReferApplicationRequest;
import ie.ul.ethics.scieng.applications.models.SubmitApplicationRequest;
import ie.ul.ethics.scieng.applications.models.UpdateDraftApplicationRequest;
import ie.ul.ethics.scieng.applications.models.ReviewSubmittedApplicationRequest;
import ie.ul.ethics.scieng.applications.models.applications.Application;
import ie.ul.ethics.scieng.applications.models.applications.ApplicationStatus;
import ie.ul.ethics.scieng.applications.models.applications.AttachedFile;
import ie.ul.ethics.scieng.applications.models.applications.Comment;
import ie.ul.ethics.scieng.applications.models.applications.DraftApplication;
import ie.ul.ethics.scieng.applications.models.applications.Answer;
import ie.ul.ethics.scieng.applications.models.applications.ReferredApplication;
import ie.ul.ethics.scieng.applications.models.applications.SubmittedApplication;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ie.ul.ethics.scieng.test.utils.constants.Users.*;
import static ie.ul.ethics.scieng.test.utils.constants.Authentication.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
        "permissions.authorization.enabled=true",
        "files.antivirus.enabled=false"
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
        Application draftApplication = createDraftApplication();
        draftApplication.setId(null);
        draftApplication.setApplicationId(null);
        CreateDraftApplicationRequest request =
                new CreateDraftApplicationRequest(USERNAME, draftApplication.getApplicationTemplate(), draftApplication.getAnswers());

        given(userService.loadUser(USERNAME))
                .willReturn(draftApplication.getUser());

        Application returned = requestMapper.createDraftRequestToDraft(request);

        assertEquals(draftApplication, returned);
        verify(userService).loadUser(USERNAME);
    }

    /**
     * Tests that an UpdateDraftRequest should be mapped correctly
     */
    @Test
    public void shouldMapUpdateDraftRequest() {
        Application draftApplication = createDraftApplication();
        ApplicationTemplate template = draftApplication.getApplicationTemplate();
        template.setDatabaseId(TEMPLATE_DB_ID);

        Map<String, Answer> oldValues = draftApplication.getAnswers();
        Map<String, Answer> newValues = new HashMap<>(oldValues);
        newValues.put("component5", new Answer(null, "component5", "answer5", Answer.ValueType.TEXT));
        draftApplication.setAnswers(newValues);

        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_ID, newValues, template);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(draftApplication);

        Application returned = requestMapper.updateDraftRequestToDraft(request);

        assertEquals(draftApplication, returned);
        assertEquals(draftApplication.getAnswers(), newValues);
        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that when updating a draft, old files should be removed from the file service if they are being updated
     */
    @Test
    public void shouldMapUpdateDraftRequestAndDeleteOldFiles() {
        Application draftApplication = createDraftApplication();
        ApplicationTemplate template = draftApplication.getApplicationTemplate();
        template.setDatabaseId(TEMPLATE_DB_ID);

        Map<String, Answer> oldValues = draftApplication.getAnswers();
        Map<String, Answer> newValues = new HashMap<>(oldValues);
        newValues.put("component5", new Answer(null, "component5", "answer5", Answer.ValueType.TEXT));

        AttachedFile old = new AttachedFile(null, "filename", "directory", USERNAME);
        AttachedFile newFile = new AttachedFile(null, "filename1", "directory", USERNAME);

        draftApplication.attachFile(old);

        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_ID, newValues, List.of(newFile), template);

        assertNotEquals(oldValues, newValues);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(draftApplication);

        Application returned = requestMapper.updateDraftRequestToDraft(request);

        assertEquals(draftApplication, returned);
        assertEquals(draftApplication.getAnswers(), newValues);
        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that an UpdateDraftRequest should be mapped correctly
     */
    @Test
    public void shouldMapUpdateReferredRequest() {
        Application draftApplication = createDraftApplication();
        User referrer = createTestUser();
        referrer.setUsername("referrer");
        referrer.setRole(Roles.CHAIR);
        Application referred = new ReferredApplication(null, APPLICATION_ID, draftApplication.getUser(), draftApplication.getApplicationTemplate(),
                draftApplication.getAnswers(), new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>(), referrer);

        ApplicationTemplate template = draftApplication.getApplicationTemplate();
        template.setDatabaseId(TEMPLATE_DB_ID);

        Map<String, Answer> oldValues = referred.getAnswers();
        Map<String, Answer> newValues = new HashMap<>(oldValues);
        newValues.put("component5", new Answer(null, "component5", "answer5", Answer.ValueType.TEXT));
        referred.setAnswers(newValues);

        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_ID, newValues, template);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(referred);

        Application returned = requestMapper.updateRequestToReferred(request);

        assertEquals(referred, returned);
        assertEquals(returned.getAnswers(), newValues);
        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Get either a submitted or referred application
     * @param submitted true to retrieve a submitted application or false for referred
     * @return the created application
     */
    private Application getSubmittedReferredApplication(boolean submitted) {
        if (submitted) {
            return new SubmittedApplication(null, APPLICATION_ID, createTestUser(), ApplicationStatus.SUBMITTED,
                    templates[0], new HashMap<>(), new ArrayList<>(), new ArrayList<>(), null);
        } else {
            return new ReferredApplication(null, APPLICATION_ID, createTestUser(),
                    templates[0], new HashMap<>(), new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>(), null);
        }
    }

    /**
     * Tests that an IllegalStateException should be thrown if the given application ID is not a draft application
     */
    @Test
    public void shouldThrowIfIDNotDraft() {
        Application changeable = getSubmittedReferredApplication(true);
        changeable.setStatus(ApplicationStatus.SUBMITTED);

        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_ID, new HashMap<>(), templates[0]);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(changeable);

        assertThrows(MappingException.class, () -> requestMapper.updateDraftRequestToDraft(request));

        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that the mapper should return null if the application ID doesn't exist
     */
    @Test
    public void shouldReturnNullIfDraftIdNotExists() {
        UpdateDraftApplicationRequest request =
                new UpdateDraftApplicationRequest(APPLICATION_ID, new HashMap<>(), templates[0]);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(null);

        assertNull(requestMapper.updateDraftRequestToDraft(request));

        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that a draft application should be mapped from the request
     */
    @Test
    public void shouldMapDraftSubmitRequest() {
        Application draft = createDraftApplication();

        SubmitApplicationRequest request = new SubmitApplicationRequest(APPLICATION_ID);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(draft);

        Application mapped = requestMapper.submitRequestToApplication(request);

        assertEquals(draft, mapped);
        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that a referred application should be mapped from the request
     */
    @Test
    public void shouldMapReferredSubmitRequest() {
        Application referred = getSubmittedReferredApplication(false);

        SubmitApplicationRequest request = new SubmitApplicationRequest(APPLICATION_ID);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(referred);

        Application mapped = requestMapper.submitRequestToApplication(request);

        assertEquals(referred, mapped);
        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that an exception should be thrown if the application is neither a draft nor a referred application
     */
    @Test
    public void shouldThrowIfApplicationIsNotDraftReferred() {
        Application submitted = getSubmittedReferredApplication(true);

        SubmitApplicationRequest request = new SubmitApplicationRequest(APPLICATION_ID);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(submitted);

        assertThrows(MappingException.class, () -> requestMapper.submitRequestToApplication(request));

        verify(applicationService).getApplication(APPLICATION_ID);
    }

    /**
     * Tests that a ReferApplicationRequest should be mapped successfully
     */
    @Test
    public void shouldMapReferApplicationRequest() {
        Application reviewed = getSubmittedReferredApplication(true);
        reviewed.setStatus(ApplicationStatus.REVIEWED);
        List<String> editableFields = new ArrayList<>();
        User referrer = createTestUser();

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(reviewed);
        given(userService.loadUser(USERNAME))
                .willReturn(referrer);

        MappedReferApplicationRequest expected = new MappedReferApplicationRequest(reviewed, editableFields, referrer);

        MappedReferApplicationRequest returned =
                requestMapper.mapReferApplicationRequest(new ReferApplicationRequest(APPLICATION_ID, editableFields, USERNAME));

        assertEquals(expected, returned);
        verify(applicationService).getApplication(APPLICATION_ID);
        verify(userService).loadUser(USERNAME);
    }

    /**
     * Tests that a request to accept a resubmitted application is mapped correctly
     */
    @Test
    public void shouldMapAcceptResubmittedRequest() {
        Application resubmitted = getSubmittedReferredApplication(true);
        resubmitted.setStatus(ApplicationStatus.RESUBMITTED);
        User chair = createTestUser();
        chair.setUsername("chair");
        chair.setRole(Roles.CHAIR);
        List<User> users = List.of(chair);

        List<String> usernames = List.of("chair");

        AcceptResubmittedRequest request = new AcceptResubmittedRequest(APPLICATION_ID, usernames);
        MappedAcceptResubmittedRequest mapped = new MappedAcceptResubmittedRequest(resubmitted, users);

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(resubmitted);
        for (int i = 0; i < usernames.size(); i++) {
            given(userService.loadUser(usernames.get(i)))
                    .willReturn(users.get(i));
        }

        MappedAcceptResubmittedRequest returned = requestMapper.mapAcceptResubmittedRequest(request);

        assertEquals(mapped, returned);
        verify(applicationService).getApplication(APPLICATION_ID);
        verify(userService, times(users.size())).loadUser(any());
    }

    /**
     * This method tests that ReviewSubmittedApplicationRequests are mapped correctly
     */
    @Test
    public void shouldMapReviewSubmittedRequest() {
        Application review = getSubmittedReferredApplication(true);
        review.setStatus(ApplicationStatus.REVIEW);
        SubmittedApplication mapped = (SubmittedApplication) getSubmittedReferredApplication(true);
        mapped.setStatus(ApplicationStatus.REVIEW);

        User user = createTestUser();

        LocalDateTime createdAt = LocalDateTime.now();

        Comment comment = new Comment(null, user, "comment", "component", new ArrayList<>(), createdAt);
        comment.addSubComment(new Comment(null, user, "comment1", "component1", new ArrayList<>(), createdAt));
        mapped.addComment(comment);

        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>(), createdAt);
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>(), createdAt));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        given(userService.loadUser(USERNAME))
                .willReturn(user);
        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(review);

        Application returned = requestMapper.reviewSubmittedRequestToSubmitted(request);

        assertEquals(mapped.getComments(), returned.getComments());
        assertTrue(returned.getComments().get(comment.getComponentId()).getComments().contains(comment));
        verify(applicationService).getApplication(APPLICATION_ID);
        verify(userService, times(2)).loadUser(USERNAME);
    }

    /**
     * Tests that if the application doesn't exist, null is returned
     */
    @Test
    public void shouldReturnNullOnReviewSubmitted() {
        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>(), LocalDateTime.now());
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>(), LocalDateTime.now()));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(null);

        Application returned = requestMapper.reviewSubmittedRequestToSubmitted(request);

        assertNull(returned);
        verify(applicationService).getApplication(APPLICATION_ID);
        verifyNoInteractions(userService);
    }

    /**
     * This method tests that An InvalidStatusException is thrown if the application in ReviewSubmittedApplicationRequests is not in review
     */
    @Test
    public void shouldThrowApplicationStatusReviewSubmittedRequest() {
        Application review = getSubmittedReferredApplication(true);
        SubmittedApplication mapped = (SubmittedApplication) getSubmittedReferredApplication(true);

        User user = createTestUser();

        Comment comment = new Comment(null, user, "comment", "component", new ArrayList<>());
        comment.addSubComment(new Comment(null, user, "comment1", "component1", new ArrayList<>()));
        mapped.addComment(comment);

        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>(), LocalDateTime.now());
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>(), LocalDateTime.now()));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        given(userService.loadUser(USERNAME))
                .willReturn(user);
        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(review);

        assertThrows(InvalidStatusException.class, () -> requestMapper.reviewSubmittedRequestToSubmitted(request));

        verify(applicationService).getApplication(APPLICATION_ID);
        verifyNoInteractions(userService);
    }

    /**
     * This method tests that an ApplicationException is thrown if a comment with a null user exists
     */
    @Test
    public void shouldThrowIfUserIsNullMapReviewSubmittedRequest() {
        Application review = getSubmittedReferredApplication(true);
        review.setStatus(ApplicationStatus.REVIEW);
        Application mapped = getSubmittedReferredApplication(true);
        mapped.setStatus(ApplicationStatus.REVIEW);

        Comment comment = new Comment(null, null, "comment", "component", new ArrayList<>());
        comment.addSubComment(new Comment(null, null, "comment1", "component1", new ArrayList<>()));
        mapped.addComment(comment);

        ReviewSubmittedApplicationRequest.Comment requestComment = new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment", "component", new ArrayList<>(), LocalDateTime.now());
        requestComment.getSubComments().add(new ReviewSubmittedApplicationRequest.Comment(null, USERNAME, "comment1", "component1", new ArrayList<>(), LocalDateTime.now()));
        ReviewSubmittedApplicationRequest request = new ReviewSubmittedApplicationRequest(APPLICATION_ID, List.of(requestComment));

        given(userService.loadUser(USERNAME))
                .willReturn(null);
        given(applicationService.getApplication(APPLICATION_ID))
                .willReturn(review);

        assertThrows(ApplicationException.class, () -> requestMapper.reviewSubmittedRequestToSubmitted(request));

        verify(applicationService).getApplication(APPLICATION_ID);
        verify(userService).loadUser(USERNAME);
    }
}
