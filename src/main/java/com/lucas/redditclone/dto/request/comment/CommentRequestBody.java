package com.lucas.redditclone.dto.request.comment;

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
public class CommentRequestBody {
	@NotBlank(message = "Comment body cannot be empty.")
	private String body;
	@NotNull(message = "Post id cannot be null.")
	private UUID postId;
	@NotNull(message = "User id cannot be null.")
	private UUID parentId;
}
