package com.lucas.redditclone.service.post;

import com.lucas.redditclone.dto.request.post.PostEditRequestBody;
import com.lucas.redditclone.dto.request.post.PostRequestBody;
import com.lucas.redditclone.dto.response.post.PostResponseBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.Role;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.enums.RoleName;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.mapper.PostMapper;
import com.lucas.redditclone.repository.PostRepository;
import com.lucas.redditclone.repository.SubRedditRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PostServiceTest {
	public static final String BODY = "body";
	public static final String URL = "url";
	public static final String TITLE = "TITLE";
	public static final String SUBREDDIT_NAME = "subredditName";
	public static final String SUB_REDDIT_DESCRIPTION = "subReddit description";
	public static final String MINUTES_AGO = "18 minutes ago";
	public static final String USER_NOT_FOUND = "User not found";
	public static final String USER_NOT_AUTHORIZED_TO_EDIT_THIS_POST = "User not authorized to edit this post.";
	public static final String USER_NOT_AUTHORIZED_TO_DELETE_THIS_POST = "User not authorized to delete this post.";
	public static final String SUB_REDDIT_NOT_FOUND = "SubReddit not found";
	private static final String NO_POSTS_FOUND = "No posts found.";
	private static final UUID ID = UUID.fromString("fa30bbb5-c704-4380-9a19-e41bfeed4ff9");
	private static final String NAME = "user";
	private static final String USERNAME = "@username";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "email@email.com";
	public static Post post;
	public static User user;
	public static Role role;
	public static SubReddit subReddit;
	public static Optional<Post> postOptional;
	public static Optional<User> userOptional;
	public static Optional<SubReddit> subRedditOptional;
	public static PostRequestBody postRequestBody;
	public static PostResponseBody postResponseBody;
	public static PostEditRequestBody postEditRequestBody;
	public static Page<Post> postsPage;
	public static Post postEdited;

	@InjectMocks
	PostServiceImpl postService;
	@Mock
	PostRepository postRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	SubRedditRepository subRedditRepository;
	@Mock
	PostMapper postMapper;

	@Mock
	AuthService authService;

	@BeforeEach
	void setUp() {
		initClasses();
	}

	@Test
	void shouldCreatePostSuccessfully() {
		when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
		when(subRedditRepository.findByName(anyString())).thenReturn(subRedditOptional);
		when(postMapper.toPost(any(PostRequestBody.class),
				any(SubReddit.class), any(User.class))).thenReturn(post);
		when(postRepository.save(any(Post.class))).thenReturn(post);
		when(postMapper.toPostResponseBody(any(Post.class))).thenReturn(postResponseBody);

		PostResponseBody response = postService.createPost(postRequestBody);

		assertEquals(PostResponseBody.class, response.getClass());
		assertEquals(postResponseBody, response);
		assertNotNull(response);
		verify(subRedditRepository, times(1)).findByName(postRequestBody.getSubRedditName());
		verify(userRepository, times(1)).findById(postRequestBody.getUserId());
		verify(postRepository, times(1)).save(post);
		verify(postMapper, times(1)).toPostResponseBody(post);
	}

	@Test
	void createPostShouldThrowBadRequestExceptionWhenUserNotFound() {
		when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
		assertThrowsExactly(BadRequestException.class,
				() -> postService.createPost(postRequestBody),
				USER_NOT_FOUND);
		verifyNoInteractions(subRedditRepository, postRepository, postMapper);
	}

	@Test
	void createPostShouldThrowBadRequestExceptionWhenSubredditNotFound() {
		when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
		when(subRedditRepository.findByName(anyString())).thenReturn(Optional.empty());
		assertThrowsExactly(BadRequestException.class,
				() -> postService.createPost(postRequestBody),
				USER_NOT_FOUND);
		verifyNoInteractions(postRepository, postMapper);
	}

	@Test
	void shouldEditAPostSuccessfully() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
		when(postMapper.toPost(any(PostEditRequestBody.class),
				any(SubReddit.class), any(User.class))).thenReturn(postEdited);
		when(postRepository.save(any(Post.class))).thenReturn(postEdited);
		when(postMapper.toPostResponseBody(any(Post.class))).thenReturn(postResponseBody);

		PostResponseBody response = postService.editPost(postEditRequestBody, ID);

		assertEquals(PostResponseBody.class, response.getClass());
		assertNotNull(response);
		assertNotEquals(post.getTitle(), postEdited.getTitle());
		assertEquals(post.getId(), postEdited.getId());
		verify(postRepository, times(1)).findById(ID);
		verify(userRepository, times(1)).findById(postEditRequestBody.getUserId());
		verify(postMapper, times(1)).toPost(postEditRequestBody, subReddit, user);
		verify(postRepository, times(1)).save(post);
	}

	@Test
	void editPostShouldThrowBadRequestExceptionWhenPostNotFound() {
		when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
		assertThrowsExactly(BadRequestException.class,
				() -> postService.editPost(postEditRequestBody, ID),
				NO_POSTS_FOUND);
		verifyNoInteractions(userRepository, postMapper);
		verify(postRepository, times(1)).findById(any(UUID.class));
		verify(postRepository, never()).save(any(Post.class));
	}

	@Test
	void editPostShouldThrowBadRequestExceptionWhenUserNotFound() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
		assertThrowsExactly(BadRequestException.class,
				() -> postService.editPost(postEditRequestBody, ID),
				USER_NOT_FOUND);
		verifyNoInteractions(postMapper);
		verify(postRepository, times(1)).findById(any(UUID.class));
		verify(userRepository, times(1)).findById(any(UUID.class));
		verify(postRepository, never()).save(any(Post.class));
	}

	@Test
	void editPostShouldThrowUnauthorizedExceptionWhenUserIsNotAuthor() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(
				User.builder().id(UUID.randomUUID()).build()
		));

		assertThrowsExactly(UnauthorizedException.class,
				() -> postService.editPost(postEditRequestBody, ID),
				USER_NOT_AUTHORIZED_TO_EDIT_THIS_POST);
		verifyNoInteractions(postMapper);
		verify(postRepository, times(1)).findById(ID);
		verify(userRepository, times(1)).findById(any(UUID.class));
		verify(postRepository, never()).save(any(Post.class));
	}

	@Test
	void shouldDeletePostSuccessfully() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(authService.getCurrentUser()).thenReturn(user);

		postService.deletePost(ID);

		verify(postRepository, times(1)).findById(ID);
		verify(postRepository, times(1)).delete(post);
		verify(authService, times(1)).getCurrentUser();
	}

	@Test
	void deletePostShouldThrowBadRequestExceptionWhenPostNotFound() {
		when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		assertThrowsExactly(BadRequestException.class, () -> postService.deletePost(ID), NO_POSTS_FOUND);
		verifyNoInteractions(authService);
		verify(postRepository, times(1)).findById(any(UUID.class));
		verify(postRepository, never()).delete(any(Post.class));
	}

	@Test
	void deletePostShouldThrowUnauthorizedExceptionWhenUserIsNotAuthor() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(authService.getCurrentUser()).thenReturn(User.builder().id(UUID.randomUUID()).build());

		assertThrowsExactly(UnauthorizedException.class,
				() -> postService.deletePost(ID),
				USER_NOT_AUTHORIZED_TO_DELETE_THIS_POST);
		verify(postRepository, times(1)).findById(ID);
		verify(authService, times(1)).getCurrentUser();
		verify(postRepository, never()).delete(any(Post.class));
	}

	@Test
	void shouldGetAllPostsByTitleWithPaginationSuccessfully() {
		when(postRepository.findAllPostsByTitleIgnoreCase(anyString(),
				any(Pageable.class))).thenReturn(postsPage);
		when(postMapper.toPostResponseBody(any(Post.class))).thenReturn(postResponseBody);

		postService.getAllPostsByTitlePageable(TITLE, PageRequest.of(0, 5));

		verify(postRepository, times(1))
				.findAllPostsByTitleIgnoreCase(anyString(), any(Pageable.class));
		verify(postMapper, atLeastOnce()).toPostResponseBody(post);
	}

	@Test
	void getAllPostsByTitleShouldThrowNotFoundExceptionIfPostPageIsEmpty() {
		when(postRepository.findAllPostsByTitleIgnoreCase(anyString(),
				any(Pageable.class))).thenReturn(Page.empty());

		assertThrowsExactly(NotFoundException.class,
				() -> postService.getAllPostsByTitlePageable(TITLE, PageRequest.of(0, 5)),
				NO_POSTS_FOUND);
		verifyNoInteractions(postMapper);
		verify(postRepository, times(1))
				.findAllPostsByTitleIgnoreCase(anyString(), any(Pageable.class));
	}

	@Test
	void shouldGetAllPostsBySubRedditWithPaginationSuccessfully() {
		when(subRedditRepository.findByName(anyString())).thenReturn(subRedditOptional);
		when(postRepository.findAllBySubReddit(any(SubReddit.class),
				any(Pageable.class))).thenReturn(postsPage);
		when(postMapper.toPostResponseBody(any(Post.class))).thenReturn(postResponseBody);

		Page<PostResponseBody> response = postService.getAllPostsBySubRedditPageable(subReddit.getName(), PageRequest.of(0, 5));

		assertEquals(PostResponseBody.class, response.toList().get(0).getClass());
		assertEquals(1, response.toList().size());
		verify(subRedditRepository, times(1)).findByName(subReddit.getName());
		verify(postRepository, times(1)).findAllBySubReddit(any(SubReddit.class), any(Pageable.class));
		verify(postMapper, atLeastOnce()).toPostResponseBody(post);
	}

	@Test
	void getAllPostsBySubRedditShouldThrowBadRequestExceptionWhenSubredditNotFound() {
		when(subRedditRepository.findByName(anyString())).thenReturn(Optional.empty());

		assertThrowsExactly(BadRequestException.class,
				() -> postService.getAllPostsBySubRedditPageable(
						subReddit.getName(),
						PageRequest.of(0, 5)),
				SUB_REDDIT_NOT_FOUND);
		verify(subRedditRepository, times(1)).findByName(subReddit.getName());
		verifyNoInteractions(postRepository, postMapper);
	}

	@Test
	void getAllPostsBySubRedditShouldThrowNotFoundExceptionIfPostPageIsEmpty() {
		when(subRedditRepository.findByName(anyString())).thenReturn(subRedditOptional);
		when(postRepository.findAllBySubReddit(any(SubReddit.class), any(Pageable.class))).thenReturn(Page.empty());

		assertThrowsExactly(NotFoundException.class,
				() -> postService.getAllPostsBySubRedditPageable(
						subReddit.getName(),
						PageRequest.of(0, 5)),
				NO_POSTS_FOUND);
		verifyNoInteractions(postMapper);
		verify(subRedditRepository, times(1)).findByName(subReddit.getName());
		verify(postRepository, times(1))
				.findAllBySubReddit(any(SubReddit.class), any(Pageable.class));
	}

	@Test
	void shouldGetAllPostsByUsernameWithPaginationSuccessfully() {
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		when(postRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(postsPage);
		when(postMapper.toPostResponseBody(any(Post.class))).thenReturn(postResponseBody);

		Page<PostResponseBody> response = postService.getAllPostsByUsernamePageable(user.getUsername(), PageRequest.of(0, 5));

		assertEquals(PostResponseBody.class, response.toList().get(0).getClass());
		assertEquals(1, response.toList().size());
		assertEquals(1, response.getNumberOfElements());
		verify(userRepository, times(1)).findByUsername(user.getUsername());
		verify(postRepository, times(1)).findAllByUser(any(User.class), any(Pageable.class));
		verify(postMapper, atLeastOnce()).toPostResponseBody(post);
	}

	@Test
	void getAllPostsByUsernameShouldThrowBadRequestExceptionWhenUserNotFound() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

		assertThrowsExactly(BadRequestException.class, () -> postService.getAllPostsByUsernamePageable(
						user.getUsername(),
						PageRequest.of(0, 5)),
				USER_NOT_FOUND);
		verify(userRepository, times(1)).findByUsername(user.getUsername());
		verifyNoInteractions(postRepository, postMapper);
	}

	@Test
	void getAllPostsByUsernameShouldThrowNotFoundExceptionIfPostPageIsEmpty() {
		when(userRepository.findByUsername(anyString())).thenReturn(userOptional);
		when(postRepository.findAllByUser(any(User.class), any(Pageable.class))).thenReturn(Page.empty());

		assertThrowsExactly(NotFoundException.class, () -> postService.getAllPostsByUsernamePageable(
						user.getUsername(),
						PageRequest.of(0, 5)),
				NO_POSTS_FOUND);
		verifyNoInteractions(postMapper);
		verify(userRepository, times(1)).findByUsername(user.getUsername());
		verify(postRepository, times(1))
				.findAllByUser(any(User.class), any(Pageable.class));
	}

	@Test
	void shouldGetAllPostsWithPaginationSuccessfully() {
		when(postRepository.findAll(any(Pageable.class))).thenReturn(postsPage);
		when(postMapper.toPostResponseBody(any(Post.class))).thenReturn(postResponseBody);

		Page<PostResponseBody> response = postService.getAllPosts(PageRequest.of(0, 5));

		assertEquals(PostResponseBody.class, response.toList().get(0).getClass());
		assertNotNull(response);
		assertEquals(1, response.toList().size());
		verify(postRepository, times(1)).findAll(any(Pageable.class));
		verify(postMapper, atLeastOnce()).toPostResponseBody(post);
	}

	@Test
	void getAllPostsShouldThrowNotFoundExceptionIfPostPageIsEmpty() {
		when(postRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

		assertThrowsExactly(NotFoundException.class,
				() -> postService.getAllPosts(PageRequest.of(0, 5)),
				NO_POSTS_FOUND);
		verify(postRepository, times(1)).findAll(any(Pageable.class));
		verifyNoInteractions(postMapper);
	}

	void initClasses() {
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

		post = Post
				.builder()
				.id(ID)
				.user(user)
				.createdAt(Instant.now())
				.body(BODY)
				.url(URL)
				.title(TITLE)
				.voteCount(0)
				.build();


		subReddit = SubReddit
				.builder()
				.user(user)
				.createdAt(Instant.now())
				.id(ID)
				.posts(List.of(post))
				.name(SUBREDDIT_NAME)
				.description(SUB_REDDIT_DESCRIPTION)
				.build();
		subRedditOptional = Optional.of(subReddit);
		post.setSubReddit(subReddit);
		postOptional = Optional.of(post);
		postsPage = new PageImpl<Post>(List.of(post));

		postRequestBody = PostRequestBody
				.builder()
				.body(BODY)
				.url(URL)
				.title(TITLE)
				.subRedditName(subReddit.getName())
				.userId(user.getId())
				.build();

		postResponseBody = PostResponseBody
				.builder()
				.body(BODY)
				.url(URL)
				.title(TITLE)
				.numberOfComments(1)
				.numberOfComments(post.getVoteCount())
				.ownerUsername(user.getUsername())
				.subRedditName(subReddit.getName())
				.timeAgo(MINUTES_AGO)
				.build();
		postEditRequestBody = PostEditRequestBody
				.builder()
				.userId(user.getId())
				.body(BODY.concat("UPDATED"))
				.title(TITLE.concat("UPDATED"))
				.url(URL.concat("UPDATED"))
				.build();

		postEdited = Post
				.builder()
				.id(ID)
				.user(user)
				.createdAt(Instant.now())
				.body(BODY.concat("UPDATED"))
				.title(TITLE.concat("UPDATED"))
				.url(URL.concat("UPDATED"))
				.subReddit(subReddit)
				.voteCount(0)
				.build();


	}
}