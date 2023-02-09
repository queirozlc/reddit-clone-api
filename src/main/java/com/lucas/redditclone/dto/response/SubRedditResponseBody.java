package com.lucas.redditclone.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lucas.redditclone.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubRedditResponseBody {
	private String name;
	private String description;
	@JsonIgnore
	private User owner;
}
