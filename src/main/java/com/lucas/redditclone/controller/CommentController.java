package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.comment.CommentRequestBody;
import com.lucas.redditclone.dto.response.comment.CommentResponseBody;
import com.lucas.redditclone.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentResponseBody> createComment(@RequestBody CommentRequestBody commentRequestBody) {
		return new ResponseEntity<>(commentService.createComment(commentRequestBody), CREATED);
	}

	@GetMapping("/user/{username}")
	public ResponseEntity<Page<CommentResponseBody>> getUserComments(@PathVariable String username,
	                                                                 Pageable pageable) {
		return ResponseEntity.ok(commentService.getAllUserComments(username, pageable));
	}

	@GetMapping("/post/{id}")
	public ResponseEntity<Page<CommentResponseBody>> getPostComments(@PathVariable UUID id, Pageable pageable) {
		return ResponseEntity.ok(commentService.getAllCommentsByPost(id, pageable));
	}
}
