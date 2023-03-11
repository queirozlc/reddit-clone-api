package com.lucas.redditclone.service.subreddit;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.not_found.NotFoundException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.mapper.SubRedditMapper;
import com.lucas.redditclone.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;

    @Override
    public SubRedditResponseBody createSubReddit(SubRedditRequestBody subRedditRequestBody) {
        SubReddit subReddit = mapSubReddit(subRedditRequestBody);

        var owner = userRepository.findById(subReddit.getUser().getId())
                .orElseThrow(() -> new BadRequestException("User not found."));

        subReddit.setUser(owner);
        SubReddit subRedditSaved = subRedditRepository.save(subReddit);
        return mapper.toSubRedditResponseBody(subRedditSaved);
    }


    @Override
    public Page<SubRedditResponseBody> getAllSubreddits(Pageable pageable) {
        Page<SubReddit> subReddits = subRedditRepository
                .findAll(pageable);

        if (subReddits.isEmpty()) {
            throw new NotFoundException(NOT_FOUND);
        }
        return subReddits.map(mapper::toSubRedditResponseBody);
    }

    @Override
    public SubRedditResponseBody updateSubReddit(SubRedditRequestBody subRedditRequestBody,
                                                 UUID subRedditId) {
        SubReddit subRedditRequest = mapSubReddit(subRedditRequestBody);
        SubReddit subRedditToBeUpdated = subRedditRepository.findById(subRedditId)
                .orElseThrow(() -> new BadRequestException(NOT_FOUND));

        if (!subRedditToBeUpdated.getUser().getId().equals(subRedditRequest.getUser().getId())) {
            throw new UnauthorizedException("You do not have access to change subreddit owner");
        }

        if (!subRedditRequest.getUri().equals(subRedditToBeUpdated.getUri()) &&
                subRedditRepository.existsByUri(subRedditRequest.getUri())) {
            throw new BadRequestException("Already exists a subreddit with this uri.");
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

    private SubReddit mapSubReddit(SubRedditRequestBody subRedditRequestBody) {
        var user = userRepository.findById(subRedditRequestBody.getUserId())
                .orElseThrow(() -> new BadRequestException("User not found."));
        var category = categoryRepository.findById(subRedditRequestBody.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Category not found."));
        return mapper.toSubReddit(subRedditRequestBody, category, user);
    }
}
