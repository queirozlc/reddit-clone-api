package com.lucas.redditclone.service.vote;

import com.lucas.redditclone.dto.request.vote.VoteRequestBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.Vote;
import com.lucas.redditclone.entity.enums.VoteType;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.mapper.VoteMapper;
import com.lucas.redditclone.repository.PostRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {
	public static final String BODY = "body";
	public static final String URL = "url";
	public static final String TITLE = "TITLE";
	private static final String NO_POSTS_FOUND = "No posts found";
	private static final String USER_NOT_FOUND = "User not found";
	private static final UUID ID = UUID.fromString("fa30bbb5-c704-4380-9a19-e41bfeed4ff9");
	private static final String NAME = "user";
	private static final String USERNAME = "@username";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "email@email.com";
	static Vote vote;
	static Optional<Vote> voteOptional;
	static VoteRequestBody voteRequestBody;
	static User user;
	static Optional<User> userOptional;
	static Post post;
	static Optional<Post> postOptional;

	@InjectMocks
	VoteServiceImpl voteService;
	@Mock
	VoteRepository voteRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	PostRepository postRepository;
	@Mock
	VoteMapper voteMapper;


	@BeforeEach
	void setUp() {
		initClasses();
	}

	@Test
	void shouldDownVoteSuccessfully() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
		when(voteRepository.findTopByPostAndUserOrderByIdDesc(any(Post.class), any(User.class))).thenReturn(voteOptional);
		when(voteMapper.toVote(any(VoteRequestBody.class), any(Post.class), any(User.class))).thenReturn(vote);
		when(voteRepository.save(any(Vote.class))).thenReturn(vote);

		voteService.vote(voteRequestBody);

		verify(postRepository, times(1)).findById(ID);
		verify(userRepository, times(1)).findById(ID);
		verify(voteRepository, times(1)).findTopByPostAndUserOrderByIdDesc(post, user);
		verify(voteMapper, times(1)).toVote(voteRequestBody, post, user);
		verify(voteRepository, times(1)).save(vote);

	}

	@Test
	void shouldUpVoteAPostSuccessfully() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
		when(voteRepository.findTopByPostAndUserOrderByIdDesc(any(Post.class), any(User.class))).thenReturn(voteOptional);
		when(voteMapper.toVote(any(VoteRequestBody.class), any(Post.class), any(User.class))).thenReturn(vote);
		when(voteRepository.save(any(Vote.class))).thenReturn(vote);
		vote.setVoteType(VoteType.DOWNVOTE);
		voteRequestBody.setVoteType(VoteType.UPVOTE);

		voteService.vote(voteRequestBody);

		verify(postRepository, times(1)).findById(ID);
		verify(userRepository, times(1)).findById(ID);
		verify(voteRepository, times(1)).findTopByPostAndUserOrderByIdDesc(post, user);
		verify(voteMapper, times(1)).toVote(voteRequestBody, post, user);
		verify(voteRepository, times(1)).save(vote);
	}

	@Test
	void voteShouldThrowBadRequestExceptionWhenPostNotFound() {
		when(postRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		assertThrowsExactly(BadRequestException.class,
				() -> voteService.vote(voteRequestBody),
				NO_POSTS_FOUND);
		verify(postRepository, times(1)).findById(ID);
		verifyNoInteractions(userRepository, voteMapper);
		verify(voteRepository, never())
				.findTopByPostAndUserOrderByIdDesc(any(Post.class), any(User.class));
		verify(voteRepository, never()).save(any(Vote.class));
	}

	@Test
	void voteShouldThrowBadRequestExceptionWhenUserNotFound() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

		assertThrowsExactly(BadRequestException.class,
				() -> voteService.vote(voteRequestBody),
				USER_NOT_FOUND);

		verify(postRepository, times(1)).findById(ID);
		verify(userRepository, times(1)).findById(ID);
		verifyNoInteractions(voteRepository, voteMapper);
	}

	@Test
	void voteShouldThrowBadRequestExceptionWhenUserAlreadyVoted() {
		when(postRepository.findById(any(UUID.class))).thenReturn(postOptional);
		when(userRepository.findById(any(UUID.class))).thenReturn(userOptional);
		when(voteRepository.findTopByPostAndUserOrderByIdDesc(any(Post.class), any(User.class))).thenReturn(voteOptional);
		voteRequestBody.setVoteType(vote.getVoteType());
		assertThrowsExactly(BadRequestException.class,
				() -> voteService.vote(voteRequestBody),
				"You have already " +
						voteRequestBody.getVoteType()
						+ "D this post.");

		verify(postRepository, times(1)).findById(ID);
		verify(userRepository, times(1)).findById(ID);
		verify(voteRepository, times(1)).findTopByPostAndUserOrderByIdDesc(post, user);
		verifyNoInteractions(voteMapper);
		verify(voteRepository, never()).save(any(Vote.class));
	}


	void initClasses() {
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

		post = Post
				.builder()
				.id(ID)
				.user(user)
				.createdAt(Instant.now())
				.body(BODY)
				.url(URL)
				.title(TITLE)
				.voteCount(0)
				.subReddit(null)
				.build();
		postOptional = Optional.of(post);

		vote = Vote
				.builder()
				.id(ID)
				.post(post)
				.voteType(VoteType.UPVOTE)
				.user(user)
				.build();

		voteOptional = Optional.of(vote);

		voteRequestBody = VoteRequestBody
				.builder()
				.voteType(VoteType.DOWNVOTE)
				.userId(user.getId())
				.postId(post.getId())
				.build();
	}
}