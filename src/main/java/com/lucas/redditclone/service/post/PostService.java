package com.lucas.redditclone.service.post;

import com.lucas.redditclone.dto.request.post.PostEditRequestBody;
import com.lucas.redditclone.dto.request.post.PostRequestBody;
import com.lucas.redditclone.dto.response.post.PostResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostService {

	PostResponseBody createPost(PostRequestBody postRequestBody);

	Page<PostResponseBody> getAllPosts(Pageable pageable);

	Page<PostResponseBody> getAllPostsByTitlePageable(String title, Pageable pageable);

	PostResponseBody editPost(PostEditRequestBody postEditRequestBody, UUID id);

	void deletePost(UUID id);

	Page<PostResponseBody> getAllPostsBySubRedditPageable(String subredditName, Pageable pageable);

	Page<PostResponseBody> getAllPostsByUsernamePageable(String username, Pageable pageable);
}
