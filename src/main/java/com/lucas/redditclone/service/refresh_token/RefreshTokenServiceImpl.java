package com.lucas.redditclone.service.refresh_token;

import com.lucas.redditclone.dto.request.refresh_token.RefreshTokenRequestBody;
import com.lucas.redditclone.dto.response.refresh_token.RefreshTokenResponseBody;
import com.lucas.redditclone.entity.RefreshToken;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.exception.unauthorized.UnauthorizedException;
import com.lucas.redditclone.mapper.RefreshTokenMapper;
import com.lucas.redditclone.repository.RefreshTokenRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.auth.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Transactional
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    public static final String USER_NOT_FOUND = "User not found.";
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenMapper mapper;
    private final UserRepository userRepository;
    private final AuthService authService;
    @Value("${cookies.expiration.expires-week}")
    private int expiresInOneWeek;


    @Override
    public RefreshToken generateRefreshToken(User user, String refreshTokenCookie) {
        validateRequest(refreshTokenCookie, user);
        return createRefreshToken(user);
    }


    @Override
    public RefreshTokenResponseBody refreshAccessToken(RefreshTokenRequestBody refreshTokenRequestBody) {
        var user = userRepository.findById(refreshTokenRequestBody.getUserId())
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
        var refreshToken = refreshTokenRepository.findByToken(refreshTokenRequestBody.getRefreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh Token not found."));

        if (!refreshToken.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to access this resource.");
        }

        if (refreshToken.getExpiredAt().isBefore(Instant.now())) {
            deleteRefreshToken(refreshToken);
            throw new UnauthorizedException("Refresh token has expired.");
        }

        RefreshToken newRefreshToken = createRefreshToken(user);
        refreshTokenRepository.delete(refreshToken);
        return mapper.toRefreshTokenResponseBody(newRefreshToken, user);
    }

    @Override
    public void logout(HttpServletResponse response) {
        var userToLogout = authService.getCurrentUser();
        var refreshToken = refreshTokenRepository.findByUser(userToLogout)
                .orElseThrow(() -> new BadRequestException("Refresh Token don't exist"));
        var cookie = new Cookie("reddit-session", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        refreshTokenRepository.delete(refreshToken);
    }

    private void validateRequest(String refreshTokenCookie, User user) {
        if (refreshTokenCookie != null &&
                !refreshTokenCookie.isEmpty() &&
                refreshTokenRepository.existsByTokenOrUser(refreshTokenCookie, user)) {
            throw new BadRequestException("User already logged in");
        }
    }

    @NotNull
    private RefreshToken createRefreshToken(User user) {
        var refreshToken = mapper.toRefreshToken(user);
        refreshToken.setExpiredAt(Instant.now().plusSeconds(expiresInOneWeek));
        return refreshTokenRepository.save(refreshToken);
    }

    private void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

}
