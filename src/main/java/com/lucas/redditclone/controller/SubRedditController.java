package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.service.SubRedditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/subreddit")
@RequiredArgsConstructor
public class SubRedditController {
	private final SubRedditService subRedditService;

	@PostMapping
	public ResponseEntity<SubRedditResponseBody> createSubreddit(@RequestBody @Valid SubRedditRequestBody subRedditRequestBody) {
		return new ResponseEntity<>(subRedditService.createSubReddit(subRedditRequestBody), CREATED);
	}
}
