package com.lucas.redditclone.service.subreddit;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.mapper.SubRedditMapper;
import com.lucas.redditclone.repository.SubRedditRepository;
import com.lucas.redditclone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubRedditServiceTest {
    public static final String BODY = "body";
    public static final String URL = "url";
    public static final String TITLE = "TITLE";
    public static final String SUBREDDIT_NAME = "subredditName";
    public static final String SUB_REDDIT_DESCRIPTION = "subReddit description";
    public static final String YOU_DO_NOT_HAVE_ACCESS_TO_CHANGE_SUBREDDIT_OWNER = "You do not have access to change subreddit owner";
    private static final String NOT_FOUND = "No subreddits found.";
    private static final UUID ID = UUID.fromString("fa30bbb5-c704-4380-9a19-e41bfeed4ff9");
    private static final String NAME = "user";
    private static final String USERNAME = "@username";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email@email.com";
    private static final String USER_NOT_FOUND = "User not found.";
    static SubReddit subReddit;
    static User user;
    static Optional<User> userOptional;
    static Post post;
    static Optional<Post> postOptional;
    static Optional<SubReddit> subRedditOptional;
    static SubRedditRequestBody subRedditRequestBody;
    static SubRedditResponseBody subRedditResponseBody;
    static List<SubReddit> subReddits;
    static Page<SubReddit> subRedditPage;
    static SubReddit subRedditRandomUser;


    @InjectMocks
    SubRedditServiceImpl subRedditService;
    @Mock
    SubRedditMapper subRedditMapper;
    @Mock
    SubRedditRepository subRedditRepository;
    @Mock
    UserRepository userRepository;


    @BeforeEach
    void setUp() {
        initClasses();
    }


    @Test
    void shouldCreateSubRedditSuccessfully() {
        when(subRedditMapper.toSubReddit(any(SubRedditRequestBody.class))).thenReturn(subReddit);
        when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
        when(subRedditRepository.save(any(SubReddit.class))).thenReturn(subReddit);
        when(subRedditMapper.toSubRedditResponseBody(any(SubReddit.class))).thenReturn(subRedditResponseBody);

        SubRedditResponseBody response = subRedditService.createSubReddit(subRedditRequestBody);

        assertEquals(SubRedditResponseBody.class, response.getClass());
        assertNotNull(response);
        assertEquals(user.getUsername(), response.getOwnerUsername());
        verify(subRedditMapper, times(1)).toSubReddit(subRedditRequestBody);
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(subRedditRepository, times(1)).save(subReddit);
    }

    @Test
    void createSubRedditShouldThrowBadRequestExceptionWhenUserNotFound() {
        when(subRedditMapper.toSubReddit(any(SubRedditRequestBody.class))).thenReturn(subReddit);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrowsExactly(BadRequestException.class, () -> subRedditService.createSubReddit(subRedditRequestBody),
                USER_NOT_FOUND);
        verifyNoInteractions(subRedditRepository);
        verify(subRedditMapper, times(1)).toSubReddit(subRedditRequestBody);
        verify(userRepository, times(1)).findById(ID);
        verify(subRedditMapper, never()).toSubRedditResponseBody(any(SubReddit.class));
    }

    @Test
    void shouldGetAllSubredditsSuccessfully() {
        when(subRedditRepository.findAll(any(Pageable.class))).thenReturn(subRedditPage);
        when(subRedditMapper.toSubRedditResponseBody(any(SubReddit.class))).thenReturn(subRedditResponseBody);

        Page<SubRedditResponseBody> response = subRedditService.getAllSubreddits(PageRequest.of(0, 10));

        assertEquals(SubRedditResponseBody.class, response.toList().get(0).getClass());
        assertEquals(1, response.toList().size());
        verify(subRedditRepository, times(1)).findAll(PageRequest.of(0, 10));
        verify(subRedditMapper, atLeastOnce()).toSubRedditResponseBody(subReddit);
    }

    @Test
    void getAllSubredditsShouldThrowNotFoundExceptionWhenNoSubredditsFound() {
        when(subRedditRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        assertThrowsExactly(NotFoundException.class,
                () -> subRedditService.getAllSubreddits(
                        PageRequest.of(0, 10)
                ),
                NOT_FOUND);

        verify(subRedditRepository, times(1)).findAll(PageRequest.of(0, 10));
        verifyNoInteractions(subRedditMapper);
    }

    @Test
    void shouldUpdateSubRedditSuccessfully() {
        when(subRedditMapper.toSubReddit(any(SubRedditRequestBody.class))).thenReturn(subReddit);
        when(subRedditRepository.findById(any(UUID.class))).thenReturn(subRedditOptional);
        when(subRedditRepository.save(any(SubReddit.class))).thenReturn(subReddit);
        when(subRedditMapper.toSubRedditResponseBody(any(SubReddit.class))).thenReturn(subRedditResponseBody);

        SubRedditResponseBody response = subRedditService.updateSubReddit(subRedditRequestBody, ID);

        assertNotNull(response);
        assertEquals(SubRedditResponseBody.class, response.getClass());
        assertEquals(user.getUsername(), response.getOwnerUsername());
        verify(subRedditMapper, times(1)).toSubReddit(subRedditRequestBody);
        verify(subRedditRepository, times(1)).findById(ID);
        verify(subRedditRepository, times(1)).save(subReddit);
        verify(subRedditMapper, times(1)).toSubRedditResponseBody(subReddit);
    }

    @Test
    void updateSubRedditShouldThrowBadRequestExceptionWhenSubRedditNotFound() {
        when(subRedditMapper.toSubReddit(any(SubRedditRequestBody.class))).thenReturn(subReddit);
        when(subRedditRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrowsExactly(
                BadRequestException.class,
                () -> subRedditService.updateSubReddit(subRedditRequestBody, ID),
                NOT_FOUND
        );

        verify(subRedditRepository, times(1)).findById(ID);
        verify(subRedditMapper, times(1)).toSubReddit(subRedditRequestBody);
        verify(subRedditMapper, never()).toSubRedditResponseBody(any(SubReddit.class));
        verify(subRedditRepository, never()).save(any(SubReddit.class));
    }

    @Test
    void updateSubRedditShouldThrowUnauthorizedExceptionWhenUserIsNotAuthor() {
        when(subRedditMapper.toSubReddit(any(SubRedditRequestBody.class))).thenReturn(subRedditRandomUser);
        when(subRedditRepository.findById(any(UUID.class))).thenReturn(subRedditOptional);

        assertThrowsExactly(
                UnauthorizedException.class,
                () -> subRedditService.updateSubReddit(
                        subRedditRequestBody, ID),
                YOU_DO_NOT_HAVE_ACCESS_TO_CHANGE_SUBREDDIT_OWNER);

        verify(subRedditRepository, times(1)).findById(ID);
        verify(subRedditMapper, times(1)).toSubReddit(subRedditRequestBody);
        verify(subRedditMapper, never()).toSubRedditResponseBody(any(SubReddit.class));
        verify(subRedditRepository, never()).save(any(SubReddit.class));
    }

    @Test
    void shouldGetAllSubRedditByNameWithPaginationSuccessfully() {
        when(subRedditRepository.findAllByNameLikeIgnoreCase(anyString(),
                any(Pageable.class))).thenReturn(subRedditPage);
        when(subRedditMapper.toSubRedditResponseBody(any(SubReddit.class)))
                .thenReturn(subRedditResponseBody);

        Page<SubRedditResponseBody> response = subRedditService
                .getAllSubRedditByNamePageable(SUBREDDIT_NAME, PageRequest.of(0, 10));

        assertEquals(SubRedditResponseBody.class, response.toList().get(0).getClass());
        assertEquals(subReddit.getName(), response.toList().get(0).getName());
        assertEquals(1, response.toList().size());
        verify(subRedditRepository, times(1))
                .findAllByNameLikeIgnoreCase(SUBREDDIT_NAME, PageRequest.of(0, 10));
        verify(subRedditMapper, atLeastOnce()).toSubRedditResponseBody(subReddit);
    }

    private void initClasses() {
        user = User
                .builder()
                .id(ID)
                .name(NAME)
                .username(USERNAME)
                .email(EMAIL)
                .password(PASSWORD)
                .enabled(false)
                .createdAt(Instant.now())
                .build();

        userOptional = Optional.of(user);

        subReddit = SubReddit
                .builder()
                .id(ID)
                .user(user)
                .description(SUB_REDDIT_DESCRIPTION)
                .name(SUBREDDIT_NAME)
                .createdAt(Instant.now())
                .build();

        post = Post
                .builder()
                .id(ID)
                .user(user)
                .createdAt(Instant.now())
                .body(BODY)
                .url(URL)
                .title(TITLE)
                .voteCount(0)
                .subReddit(subReddit)
                .build();
        postOptional = Optional.of(post);

        subReddit.setPosts(List.of(post));
        subRedditOptional = Optional.of(subReddit);
        subRedditPage = new PageImpl<>(List.of(subReddit));
        subReddits = List.of(subReddit);

        subRedditRequestBody = SubRedditRequestBody
                .builder()
                .userId(user.getId())
                .description(SUB_REDDIT_DESCRIPTION)
                .name(SUBREDDIT_NAME)
                .build();

        subRedditResponseBody = SubRedditResponseBody
                .builder()
                .name(SUBREDDIT_NAME)
                .description(SUB_REDDIT_DESCRIPTION)
                .ownerUsername(user.getUsername())
                .numberOfPosts(1L)
                .build();

        subRedditRandomUser = SubReddit
                .builder()
                .id(ID)
                .user(User.builder().id(UUID.randomUUID()).build())
                .description(SUB_REDDIT_DESCRIPTION)
                .name(SUBREDDIT_NAME)
                .createdAt(Instant.now())
                .build();
    }
}