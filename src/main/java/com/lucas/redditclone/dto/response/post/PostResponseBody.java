package com.lucas.redditclone.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostResponseBody {
	private String title;
	private String body;
	private String url;
	private int voteCount;
	private String subRedditName;
	private String ownerUsername;
}
