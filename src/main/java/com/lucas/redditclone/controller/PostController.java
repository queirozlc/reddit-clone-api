package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.post.PostEditRequestBody;
import com.lucas.redditclone.dto.request.post.PostRequestBody;
import com.lucas.redditclone.dto.response.post.PostResponseBody;
import com.lucas.redditclone.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

	private final PostService postService;

	@PostMapping
	public ResponseEntity<PostResponseBody> createPost(@RequestBody @Valid PostRequestBody postRequestBody) {
		return new ResponseEntity<>(postService.createPost(postRequestBody), CREATED);
	}

	@GetMapping
	public ResponseEntity<Page<PostResponseBody>> getAllPosts(Pageable pageable) {
		return ResponseEntity.ok(postService.getAllPosts(pageable));
	}

	@GetMapping("/subReddit/{subRedditName}")
	public ResponseEntity<Page<PostResponseBody>> getPostsBySubreddit(Pageable pageable, @PathVariable String subRedditName) {
		return ResponseEntity.ok(postService.getAllPostsBySubRedditPageable(subRedditName, pageable));
	}

	@GetMapping("/user/{username}")
	public ResponseEntity<Page<PostResponseBody>> getPostsByUser(Pageable pageable, @PathVariable String username) {
		return ResponseEntity.ok(postService.getAllPostsByUsernamePageable(username, pageable));
	}

	@GetMapping("/search/post")
	public ResponseEntity<Page<PostResponseBody>> getPostsByTitle(@RequestParam String title,
	                                                              Pageable pageable) {
		return ResponseEntity.ok(postService.getAllPostsByTitlePageable(title, pageable));
	}

	@PutMapping("/{id}")
	public ResponseEntity<PostResponseBody> editPost(@PathVariable UUID id,
	                                                 @RequestBody @Valid PostEditRequestBody postEditRequestBody) {
		return ResponseEntity.ok(postService.editPost(postEditRequestBody, id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePost(@PathVariable UUID id) {
		postService.deletePost(id);
		return ResponseEntity.ok("Post deleted successfully.");
	}
}
