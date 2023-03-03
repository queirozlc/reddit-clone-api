package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.service.subreddit.SubRedditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/subreddits")
@RequiredArgsConstructor
public class SubRedditController {
	private final SubRedditService subRedditService;

	@PostMapping
	public ResponseEntity<SubRedditResponseBody> createSubreddit(@RequestBody @Valid SubRedditRequestBody subRedditRequestBody) {
		return new ResponseEntity<>(subRedditService.createSubReddit(subRedditRequestBody), CREATED);
	}

	@GetMapping
	public ResponseEntity<Page<SubRedditResponseBody>> getAllSubReddits(Pageable pageable) {
		return ResponseEntity.ok(subRedditService.getAllSubreddits(pageable));
	}

	@GetMapping("/search/subreddit")
	public ResponseEntity<Page<SubRedditResponseBody>> getSubRedditPageable(@RequestParam String name,
	                                                                        Pageable pageable) {
		return ResponseEntity.ok(subRedditService.getAllSubRedditByNamePageable(name, pageable));
	}

	@PutMapping("{id}")
	public ResponseEntity<SubRedditResponseBody> updateSubreddit(@RequestBody @Valid SubRedditRequestBody subRedditRequestBody, @PathVariable UUID id) {
		return ResponseEntity.ok(subRedditService.updateSubReddit(subRedditRequestBody, id));
	}
}
