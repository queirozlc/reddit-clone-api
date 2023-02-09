package com.lucas.redditclone.service.impl;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.mapper.SubRedditMapper;
import com.lucas.redditclone.repository.SubRedditRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.SubRedditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@Transactional
@RequiredArgsConstructor
public class SubRedditServiceImpl implements SubRedditService {
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
}
