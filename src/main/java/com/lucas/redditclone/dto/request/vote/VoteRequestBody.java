package com.lucas.redditclone.dto.request.vote;

import com.lucas.redditclone.entity.enums.VoteType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoteRequestBody {
	@NotNull(message = "Vote type is required.")
	VoteType voteType;
	@NotNull(message = "Id of post is required.")
	private UUID postId;
	@NotNull(message = "Id of user is required.")
	private UUID userId;
}
