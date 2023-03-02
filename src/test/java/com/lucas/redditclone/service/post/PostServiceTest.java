package com.lucas.redditclone.service.post;

import com.lucas.redditclone.dto.request.post.PostEditRequestBody;
import com.lucas.redditclone.dto.request.post.PostRequestBody;
import com.lucas.redditclone.dto.response.post.PostResponseBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.Role;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.enums.RoleName;
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
	public static Page<PostResponseBody> postResponseBodyPage;

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
	void shouldDeletePostSuccessfully() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(authService.getCurrentUser()).thenReturn(user);

		postService.deletePost(ID);

		verify(postRepository, times(1)).findById(ID);
		verify(postRepository, times(1)).delete(post);
		verify(authService, times(1)).getCurrentUser();
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