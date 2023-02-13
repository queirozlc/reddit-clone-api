package com.lucas.redditclone.dto.response.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommentResponseBody {
	private String body;
	private String postTitle;
	private String parentUsername;
	private String subRedditName;
}
