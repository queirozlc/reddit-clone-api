package com.lucas.redditclone.dto.response.refresh_token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lucas.redditclone.entity.RefreshToken;
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
    @JsonIgnore
    private String refreshToken;
    @JsonIgnore
    private RefreshToken refreshTokenEntity;
}
