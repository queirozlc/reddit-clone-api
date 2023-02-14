package com.lucas.redditclone.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEditRequestBody {
	@NotBlank(message = "Title is required")
	private String title;
	private String url;
	private String body;
	@NotNull(message = "Id from user is required.")
	private UUID userId;
}
