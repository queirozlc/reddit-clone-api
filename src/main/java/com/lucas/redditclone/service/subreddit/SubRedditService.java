package com.lucas.redditclone.service.subreddit;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SubRedditService {
	SubRedditResponseBody createSubReddit(SubRedditRequestBody subRedditRequestBody);

	List<SubRedditResponseBody> getAllSubreddits();

	SubRedditResponseBody updateSubReddit(SubRedditRequestBody subRedditRequestBody, UUID subRedditId);

	Page<SubRedditResponseBody> getAllSubRedditByNamePageable(String name, Pageable pageable);
}
