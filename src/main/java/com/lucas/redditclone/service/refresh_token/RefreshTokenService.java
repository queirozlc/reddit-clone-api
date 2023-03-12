package com.lucas.redditclone.service.refresh_token;

import com.lucas.redditclone.dto.request.refresh_token.RefreshTokenRequestBody;
import com.lucas.redditclone.dto.response.refresh_token.RefreshTokenResponseBody;
import com.lucas.redditclone.entity.RefreshToken;
import com.lucas.redditclone.entity.User;
import jakarta.servlet.http.HttpServletResponse;

public interface RefreshTokenService {

    RefreshToken generateRefreshToken(User user, String refreshTokenCookie);

    RefreshTokenResponseBody refreshAccessToken(RefreshTokenRequestBody refreshTokenRequestBody);

    void logout(HttpServletResponse response);

}
