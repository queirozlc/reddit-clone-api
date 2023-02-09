package com.lucas.redditclone.service;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;

public interface SubRedditService {
	SubRedditResponseBody createSubReddit(SubRedditRequestBody subRedditRequestBody);
}
