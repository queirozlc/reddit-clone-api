package com.lucas.redditclone.dto.response.refresh_token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenResponseBody {
	private String newAccessToken;
	private String username;
}
