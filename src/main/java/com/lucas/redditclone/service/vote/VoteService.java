package com.lucas.redditclone.service.vote;

import com.lucas.redditclone.dto.request.vote.VoteRequestBody;

public interface VoteService {

	void vote(VoteRequestBody voteRequestBody);
}
