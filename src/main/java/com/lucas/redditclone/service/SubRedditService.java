package com.lucas.redditclone.service;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;

import java.util.List;
import java.util.UUID;

public interface SubRedditService {
	SubRedditResponseBody createSubReddit(SubRedditRequestBody subRedditRequestBody);

	List<SubRedditResponseBody> getAllSubreddits();

	SubRedditResponseBody updateSubReddit(SubRedditRequestBody subRedditRequestBody, UUID subRedditId);
}
