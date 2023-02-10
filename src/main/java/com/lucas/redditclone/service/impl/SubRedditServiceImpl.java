package com.lucas.redditclone.service.impl;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.mapper.SubRedditMapper;
import com.lucas.redditclone.repository.SubRedditRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.SubRedditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class SubRedditServiceImpl implements SubRedditService {
	private static final String NOT_FOUND = "No subreddits found.";
	private final SubRedditMapper mapper;
	private final SubRedditRepository subRedditRepository;
	private final UserRepository userRepository;

	@Override
	public SubRedditResponseBody createSubReddit(SubRedditRequestBody subRedditRequestBody) {
		SubReddit subReddit = mapper.toSubReddit(subRedditRequestBody);

		if (subReddit.getUser().getId() == null) {
			throw new BadRequestException("The subreddit must have an owner.");
		}
		var owner = userRepository.findById(subReddit.getUser().getId())
				.orElseThrow(() -> new BadRequestException("User not found."));

		subReddit.setUser(owner);
		subReddit.setCreatedAt(Instant.now());
		SubReddit subRedditSaved = subRedditRepository.save(subReddit);
		return mapper.toSubRedditResponseBody(subRedditSaved);
	}

	@Override
	public List<SubRedditResponseBody> getAllSubreddits() {
		List<SubRedditResponseBody> subreddits = subRedditRepository
				.findAll()
				.stream()
				.map(mapper::toSubRedditResponseBody)
				.toList();
		if (subreddits.isEmpty()) {
			throw new NotFoundException(NOT_FOUND);
		}
		return subreddits;
	}

	@Override
	public SubRedditResponseBody updateSubReddit(SubRedditRequestBody subRedditRequestBody,
	                                             UUID subRedditId) {
		SubReddit subRedditRequest = mapper.toSubReddit(subRedditRequestBody);
		SubReddit subRedditToBeUpdated = subRedditRepository.findById(subRedditId)
				.orElseThrow(() -> new BadRequestException(NOT_FOUND));

		if (!subRedditToBeUpdated.getUser().getId().equals(subRedditRequest.getUser().getId())) {
			subRedditToBeUpdated.getUser().setEnabled(false);
			throw new BadRequestException("The owner of subReddit cannot be changed.");
		}

		subRedditRequest.setId(subRedditToBeUpdated.getId());
		subRedditRequest.setPosts(subRedditToBeUpdated.getPosts());
		subRedditRequest.setUser(subRedditToBeUpdated.getUser());
		subRedditRequest.setCreatedAt(subRedditToBeUpdated.getCreatedAt());
		subRedditRequest.setUpdatedAt(Instant.now());
		SubReddit subRedditUpdated = subRedditRepository.save(subRedditRequest);
		return mapper.toSubRedditResponseBody(subRedditUpdated);
	}

}
