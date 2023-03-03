package com.lucas.redditclone.service.subreddit;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.mapper.SubRedditMapper;
import com.lucas.redditclone.repository.SubRedditRepository;
import com.lucas.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
		SubReddit subRedditSaved = subRedditRepository.save(subReddit);
		return mapper.toSubRedditResponseBody(subRedditSaved);
	}

	@Override
	public Page<SubRedditResponseBody> getAllSubreddits(Pageable pageable) {
		Page<SubRedditResponseBody> subRedditResponseBodyPage = subRedditRepository
				.findAll(pageable)
				.map(mapper::toSubRedditResponseBody);

		if (subRedditResponseBodyPage.isEmpty()) {
			throw new NotFoundException(NOT_FOUND);
		}
		return subRedditResponseBodyPage;
	}

	@Override
	public SubRedditResponseBody updateSubReddit(SubRedditRequestBody subRedditRequestBody,
	                                             UUID subRedditId) {
		SubReddit subRedditRequest = mapper.toSubReddit(subRedditRequestBody);
		SubReddit subRedditToBeUpdated = subRedditRepository.findById(subRedditId)
				.orElseThrow(() -> new BadRequestException(NOT_FOUND));

		if (!subRedditToBeUpdated.getUser().getId().equals(subRedditRequest.getUser().getId())) {
			throw new UnauthorizedException("You do not have access to change subreddit owner");
		}

		subRedditRequest.setId(subRedditToBeUpdated.getId());
		subRedditRequest.setPosts(subRedditToBeUpdated.getPosts());
		subRedditRequest.setUser(subRedditToBeUpdated.getUser());
		subRedditRequest.setCreatedAt(subRedditToBeUpdated.getCreatedAt());
		subRedditRequest.setUpdatedAt(Instant.now());
		SubReddit subRedditUpdated = subRedditRepository.save(subRedditRequest);
		return mapper.toSubRedditResponseBody(subRedditUpdated);
	}

	@Override
	public Page<SubRedditResponseBody> getAllSubRedditByNamePageable(String name, Pageable pageable) {
		Page<SubReddit> subReddits = subRedditRepository
				.findAllByNameLikeIgnoreCase(name, pageable);
		return subReddits.map(mapper::toSubRedditResponseBody);
	}
}
