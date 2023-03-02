package com.lucas.redditclone.service.comment;

import com.lucas.redditclone.dto.request.comment.CommentRequestBody;
import com.lucas.redditclone.dto.response.MailResponseBody;
import com.lucas.redditclone.dto.response.comment.CommentResponseBody;
import com.lucas.redditclone.entity.*;
import com.lucas.redditclone.entity.enums.RoleName;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.mapper.CommentMapper;
import com.lucas.redditclone.repository.CommentRepository;
import com.lucas.redditclone.repository.PostRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.auth.AuthService;
import com.lucas.redditclone.service.impl.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CommentServiceTest {

	public static final String SUB_REDDIT_NAME = "Subreddit";
	public static final String NO_POSTS_FOUND = "No posts found.";
	public static final String USER_NOT_FOUND = "User not found.";
	public static final String USER_HAVE_NO_COMMENTS_YET = "User have no comments yet.";
	public static final String COMMENT_NOT_FOUND = "Comment not found";
	public static final String NOT_ALLOWED_TO_DELETE_THIS_COMMENT = "You are not allowed to delete this comment.";
	public static final String BODY = "body";
	public static final String URL = "url";
	public static final String TITLE = "TITLE";
	private static final UUID ID = UUID.fromString("fa30bbb5-c704-4380-9a19-e41bfeed4ff9");
	private static final String NAME = "user";
	private static final String USERNAME = "@username";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "email@email.com";
	public static Comment comment;
	public static Post post;
	public static User user;
	public static Role role;
	public static SubReddit subReddit;
	public static CommentRequestBody commentRequestBody;
	public static CommentResponseBody commentResponseBody;
	public static Optional<Comment> commentOptional;
	public static Optional<Post> postOptional;
	public static Optional<User> userOptional;
	public static Page<Comment> commentPage;

	@Spy
	@InjectMocks
	CommentServiceImpl commentService;
	@Mock
	CommentRepository commentRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	PostRepository postRepository;
	@Mock
	CommentMapper commentMapper;
	@Mock
	EmailService emailService;
	@Mock
	AuthService authService;


	@BeforeEach
	void setUp() {
		initClasses();
	}

	@Test
	void shouldCreateCommentSuccessfully() {
		when(postRepository.findById(ID)).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
		when(commentMapper.toComment(any(CommentRequestBody.class),
				any(Post.class), any(User.class))).thenReturn(comment);
		when(commentRepository.save(any(Comment.class))).thenReturn(comment);
		doNothing().when(commentService).sendEmail(any(Comment.class));
		when(commentMapper.toCommentResponseBody(any(Comment.class))).thenReturn(commentResponseBody);

		CommentResponseBody response = commentService.createComment(commentRequestBody);

		assertEquals(CommentResponseBody.class, response.getClass());
		verify(postRepository, times(1)).findById(ID);
		verify(userRepository, times(1)).findById(ID);
		verify(commentMapper, times(1))
				.toComment(commentRequestBody, post, user);
		verify(commentRepository, times(1)).save(comment);
	}

	@Test
	void createCommentShouldThrowBadRequestExceptionIfPostNotFound() {
		when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		try {
			commentService.createComment(commentRequestBody);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(NO_POSTS_FOUND, ex.getMessage());
			verify(postRepository, times(1)).findById(any(UUID.class));
			verifyNoMoreInteractions(userRepository);
			verifyNoMoreInteractions(commentMapper);
		}
	}

	@Test
	void createCommentShouldThrowBadRequestExceptionIfUserDoesNotExists() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		try {
			commentService.createComment(commentRequestBody);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(USER_NOT_FOUND, ex.getMessage());
			verify(postRepository, times(1)).findById(any(UUID.class));
			verify(userRepository, times(1)).findById(any(UUID.class));
			verifyNoMoreInteractions(commentMapper);
			verifyNoMoreInteractions(commentRepository);
		}
	}

	@Test
	void createCommentShouldGetAllCommentsByPostSuccessfully() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(commentRepository.findAllByPost(any(Post.class), any(Pageable.class))).thenReturn(commentPage);
		when(commentMapper.toCommentResponseBody(any(Comment.class))).thenReturn(commentResponseBody);

		Page<CommentResponseBody> response = commentService.getAllCommentsByPost(ID,
				PageRequest.of(0, 10));

		assertEquals(PageImpl.class, response.getClass());
		assertEquals(CommentResponseBody.class, response.toList().get(0).getClass());
		verify(postRepository, times(1)).findById(ID);
		verify(commentRepository, times(1)).findAllByPost(any(Post.class), any(Pageable.class));
		verify(commentMapper, atLeastOnce()).toCommentResponseBody(any(Comment.class));
	}

	@Test
	void getAllCommentsByPostShouldThrowBadRequestExceptionIfPostNotFound() {
		when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		try {
			commentService.getAllCommentsByPost(ID, PageRequest.of(0, 10));
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(NO_POSTS_FOUND, ex.getMessage());
			verify(postRepository, times(1)).findById(any(UUID.class));
			verifyNoMoreInteractions(commentRepository);
			verifyNoMoreInteractions(commentMapper);
		}
	}

	@Test
	void shouldGetAllUserCommentsSuccessfully() {
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		when(commentRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(commentPage);
		when(commentMapper.toCommentResponseBody(any(Comment.class))).thenReturn(commentResponseBody);

		Page<CommentResponseBody> response = commentService.getAllUserComments(USERNAME, PageRequest.of(0, 10));

		assertEquals(PageImpl.class, response.getClass());
		assertEquals(CommentResponseBody.class, response.toList().get(0).getClass());
		assertEquals(user.getUsername(), response.toList().get(0).getParentUsername());
		verify(userRepository, times(1)).findByUsername(anyString());
		verify(commentRepository, times(1)).findAllByUser(any(User.class), any(Pageable.class));
		verify(commentMapper, atLeastOnce()).toCommentResponseBody(any(Comment.class));
	}

	@Test
	void getAllUserCommentsShouldThrowBadRequestExceptionIfUserNotFound() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

		try {
			commentService.getAllUserComments(USERNAME, PageRequest.of(0, 10));
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(USER_NOT_FOUND, ex.getMessage());
			verify(userRepository, times(1)).findByUsername(anyString());
			verifyNoMoreInteractions(commentRepository);
			verifyNoMoreInteractions(commentMapper);
		}
	}

	@Test
	void getAllUserCommentsShouldThrowNotFoundExceptionIfUserHasNoComments() {
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		when(commentRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(Page.empty());

		try {
			commentService.getAllUserComments(USERNAME, PageRequest.of(0, 10));
		}
		catch (Exception ex) {
			assertEquals(NotFoundException.class, ex.getClass());
			assertEquals(USER_HAVE_NO_COMMENTS_YET, ex.getMessage());
			verify(userRepository, times(1)).findByUsername(anyString());
			verify(commentRepository, times(1)).findAllByUser(any(User.class),
					any(Pageable.class));
			verifyNoMoreInteractions(commentMapper);
		}
	}

	@Test
	void shouldDeleteCommentSuccessfully() {
		when(commentRepository.findById(any(UUID.class))).thenReturn(commentOptional);
		when(authService.getCurrentUser()).thenReturn(user);

		commentService.delete(ID);

		verify(commentRepository, times(1)).findById(any(UUID.class));
		verify(authService, times(1)).getCurrentUser();
		verify(commentRepository, times(1)).delete(any(Comment.class));

	}

	@Test
	void deleteShouldThrowBadRequestExceptionIfCommentNotFound() {
		when(commentRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		try {
			commentService.delete(ID);
		}
		catch (Exception ex) {
			assertEquals(BadRequestException.class, ex.getClass());
			assertEquals(COMMENT_NOT_FOUND, ex.getMessage());
			verify(commentRepository, times(1)).findById(any(UUID.class));
			verifyNoMoreInteractions(authService);
			verifyNoMoreInteractions(commentRepository);
		}
	}

	@Test
	void deleteShouldThrowUnauthorizedExceptionIfUserNotAllowedToDeleteComment() {
		when(commentRepository.findById(any(UUID.class))).thenReturn(commentOptional);
		when(authService.getCurrentUser()).thenReturn(User.builder().id(UUID.randomUUID()).build());

		try {
			commentService.delete(UUID.randomUUID());
		}
		catch (Exception ex) {
			assertEquals(UnauthorizedException.class, ex.getClass());
			assertEquals(NOT_ALLOWED_TO_DELETE_THIS_COMMENT, ex.getMessage());
			verify(commentRepository, times(1)).findById(any(UUID.class));
			verify(authService, times(1)).getCurrentUser();
			verifyNoMoreInteractions(commentRepository);
		}
	}

	@Test
	void shouldSendEmailSuccessfully() {
		doNothing().when(emailService).sendEmail(any(MailResponseBody.class));

		commentService.sendEmail(comment);

		verify(emailService, times(1)).sendEmail(any(MailResponseBody.class));
	}

	private void initClasses() {
		role = Role.builder().id(ID).name(RoleName.ROLE_USER).build();

		user = User
				.builder()
				.id(ID)
				.name(NAME)
				.username(USERNAME)
				.email(EMAIL)
				.password(PASSWORD)
				.enabled(false)
				.createdAt(Instant.now())
				.role(role)
				.build();
		userOptional = Optional.of(user);

		subReddit = SubReddit.builder().build();

		post = Post
				.builder()
				.id(ID)
				.user(user)
				.createdAt(Instant.now())
				.body(BODY)
				.url(URL)
				.subReddit(subReddit)
				.title(TITLE)
				.voteCount(0)
				.build();
		postOptional = Optional.of(post);


		commentRequestBody = CommentRequestBody
				.builder()
				.body(BODY)
				.parentId(user.getId())
				.postId(post.getId())
				.build();

		commentResponseBody = CommentResponseBody.
				builder()
				.body(BODY)
				.parentUsername(user.getUsername())
				.postTitle(post.getTitle())
				.subRedditName(SUB_REDDIT_NAME)
				.build();

		comment = Comment
				.builder()
				.id(ID)
				.user(user)
				.post(post)
				.createdAt(Instant.now())
				.body(BODY)
				.build();

		commentOptional = Optional.of(comment);

		commentPage = new PageImpl<Comment>(List.of(comment));
	}
}