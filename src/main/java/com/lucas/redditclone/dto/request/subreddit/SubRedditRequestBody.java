package com.lucas.redditclone.dto.request.subreddit;

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
public class SubRedditRequestBody {
	@NotBlank(message = "Field name is required.")
	@Pattern(regexp = "(?:^| )(/?r/[a-z]+)", message = "Invalid name. Try something like that: 'r/memes'.")
	private String name;
	@NotBlank(message = "Field description is required.")
	private String description;
	@NotNull(message = "Id of owner user is required.")
	private UUID userId;
}
