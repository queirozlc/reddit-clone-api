package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.service.SubRedditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/subreddit")
@RequiredArgsConstructor
public class SubRedditController {
	private final SubRedditService subRedditService;

	@PostMapping
//	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<SubRedditResponseBody> createSubreddit(@RequestBody @Valid SubRedditRequestBody subRedditRequestBody) {
		return new ResponseEntity<>(subRedditService.createSubReddit(subRedditRequestBody), CREATED);
	}

	@GetMapping
//	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<List<SubRedditResponseBody>> getAllSubReddits() {
		return ResponseEntity.ok(subRedditService.getAllSubreddits());
	}

	@PutMapping("{id}")
	public ResponseEntity<SubRedditResponseBody> updateSubreddit(@RequestBody @Valid SubRedditRequestBody subRedditRequestBody, @PathVariable UUID id) {
		return ResponseEntity.ok(subRedditService.updateSubReddit(subRedditRequestBody, id));
	}
}
