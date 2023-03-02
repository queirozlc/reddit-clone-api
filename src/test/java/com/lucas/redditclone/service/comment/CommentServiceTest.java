package com.lucas.redditclone.service.comment;

import com.lucas.redditclone.dto.request.comment.CommentRequestBody;
import com.lucas.redditclone.dto.response.MailResponseBody;
import com.lucas.redditclone.dto.response.comment.CommentResponseBody;
import com.lucas.redditclone.entity.*;
import com.lucas.redditclone.entity.enums.RoleName;
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

	public static final String BODY = "body";
	public static final String URL = "url";
	public static final String TITLE = "TITLE";
	public static final String SUB_REDDIT_NAME = "Subreddit";
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
	void shouldGetAllCommentsByPostSuccessfully() {
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
	void shouldDeleteCommentSuccessfully() {
		when(commentRepository.findById(any(UUID.class))).thenReturn(commentOptional);
		when(authService.getCurrentUser()).thenReturn(user);

		commentService.delete(ID);

		verify(commentRepository, times(1)).findById(any(UUID.class));
		verify(authService, times(1)).getCurrentUser();
		verify(commentRepository, times(1)).delete(any(Comment.class));

	}

	@Test
	void shouldSendEmailSuccessfully() {
		doNothing().when(emailService).sendEmail(any(MailResponseBody.class));

		commentService.sendEmail(comment);

		verify(emailService, times(1)).sendEmail(any(MailResponseBody.class));
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

		role = Role.builder().id(ID).name(RoleName.ROLE_USER).build();


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