package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.vote.VoteRequestBody;
import com.lucas.redditclone.service.vote.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/votes")
public class VoteController {
	private final VoteService voteService;

	@PostMapping
	public ResponseEntity<Void> vote(@RequestBody @Valid VoteRequestBody voteRequestBody) {
		voteService.vote(voteRequestBody);
		return ResponseEntity.ok().build();
	}
}
