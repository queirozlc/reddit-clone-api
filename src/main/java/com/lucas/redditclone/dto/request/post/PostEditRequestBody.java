package com.lucas.redditclone.dto.request.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEditRequestBody {
	@NotBlank(message = "Title is required")
	private String title;
	private String url;
	private String body;
}
