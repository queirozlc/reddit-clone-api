package com.lucas.redditclone.service.post;

import com.lucas.redditclone.dto.request.post.PostEditRequestBody;
import com.lucas.redditclone.dto.request.post.PostRequestBody;
import com.lucas.redditclone.dto.response.post.PostResponseBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.mapper.PostMapper;
import com.lucas.redditclone.repository.PostRepository;
import com.lucas.redditclone.repository.SubRedditRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class PostServiceImpl implements PostService {
	private static final String NO_POSTS_FOUND = "No posts found.";
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final SubRedditRepository subRedditRepository;
	private final PostMapper mapper;
	private final AuthService authService;

	@Override
	public PostResponseBody createPost(PostRequestBody postRequestBody) {
		var user = userRepository.findById(postRequestBody.getUserId())
				.orElseThrow(() -> new BadRequestException("User not found"));
		var subReddit = subRedditRepository.findByName(postRequestBody.getSubRedditName())
				.orElseThrow(() -> new BadRequestException("SubReddit not found."));
		var post = mapper.toPost(postRequestBody, subReddit, user);
		var postSaved = postRepository.save(post);
		return mapper.toPostResponseBody(postSaved);
	}

	@Override
	public PostResponseBody editPost(PostEditRequestBody postEditRequestBody, UUID id) {
		var postToBeEdited = postRepository.findById(id)
				.orElseThrow(() -> new BadRequestException(NO_POSTS_FOUND));
		var user = userRepository.findById(postEditRequestBody.getUserId())
				.orElseThrow(() -> new BadRequestException("User not found"));

		if (!user.getId().equals(postToBeEdited.getUser().getId())) {
			throw new UnauthorizedException("User not authorized to edit this post.");
		}

		var post = mapper.toPost(postEditRequestBody, postToBeEdited.getSubReddit(), postToBeEdited.getUser());

		post.setId(postToBeEdited.getId());
		post.setCreatedAt(postToBeEdited.getCreatedAt());
		Post postUpdated = postRepository.save(post);
		return mapper.toPostResponseBody(postUpdated);
	}

	@Override
	public void deletePost(UUID id) {
		Post post = postRepository
				.findById(id)
				.orElseThrow(() -> new BadRequestException(NO_POSTS_FOUND));

		if (!post.getUser().getId().equals(authService.getCurrentUser().getId())) {
			throw new UnauthorizedException("User not authorized to delete this post.");
		}

		postRepository.delete(post);
	}

	@Override
	public Page<PostResponseBody> getAllPostsByTitlePageable(String title, Pageable pageable) {
		var posts = postRepository.findAllPostsByTitleIgnoreCase(title, pageable);

		if (posts.isEmpty()) {
			throw new NotFoundException(NO_POSTS_FOUND);
		}

		return posts.map(mapper::toPostResponseBody);
	}

	@Override
	public Page<PostResponseBody> getAllPostsBySubRedditPageable(String subredditName, Pageable pageable) {
		SubReddit subReddit = subRedditRepository
				.findByName(subredditName)
				.orElseThrow(() -> new BadRequestException("SubReddit not found"));

		Page<Post> postsBySubReddit = postRepository.findAllBySubReddit(subReddit, pageable);

		if (postsBySubReddit.isEmpty()) {
			throw new NotFoundException(NO_POSTS_FOUND);
		}

		return postsBySubReddit.map(mapper::toPostResponseBody);
	}

	@Override
	public Page<PostResponseBody> getAllPostsByUsernamePageable(String username, Pageable pageable) {
		var user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BadRequestException("User not found."));

		Page<Post> postsByUser = postRepository.findAllByUser(user, pageable);

		if (postsByUser.isEmpty()) {
			throw new NotFoundException(NO_POSTS_FOUND);
		}

		return postsByUser.map(mapper::toPostResponseBody);
	}

	@Override
	public Page<PostResponseBody> getAllPosts(Pageable pageable) {
		Page<Post> posts = postRepository.findAll(pageable);

		if (posts.isEmpty()) {
			throw new NotFoundException(NO_POSTS_FOUND);
		}

		return posts.map(mapper::toPostResponseBody);
	}
}
