package com.lucas.redditclone.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestBody {
	@NotBlank(message = "Subreddit name is required.")
	@Pattern(regexp = "(?:^| )(/?r/[a-zA-Z]+)", message = "Invalid name. Try something like that: 'r/memes'.")
	private String subRedditName;
	@NotBlank(message = "Post title is required.")
	private String title;
	@NotNull(message = "User id is required.")
	private UUID userId;
	private String body;
	private String url;
}
