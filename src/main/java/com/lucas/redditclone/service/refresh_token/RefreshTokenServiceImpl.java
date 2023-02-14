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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.lucas.redditclone.util.cookie.CookieUtil.*;

@Transactional
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final RefreshTokenMapper mapper;
	private final UserRepository userRepository;
	@Value("${cookies.expiration.expires-year}")
	private int expiresInOneYear;
	@Value("${cookies.expiration.expires-day}")
	private int expiresInOneDay;
	@Value("${cookies.key.cookie-name}")
	private String key;

	@Override
	public RefreshToken generateRefreshToken(User user,
	                                         boolean rememberMe,
	                                         HttpServletResponse response, HttpServletRequest request) {
		validateRequest(request);
		var refreshToken = mapper.toRefreshToken(user);
		createCookie(response,
				key,
				refreshToken.getToken(),
				"localhost",
				rememberMe ? expiresInOneYear : expiresInOneDay,
				false);
		refreshToken.setExpiredAt(rememberMe ? Instant.now().plusSeconds(expiresInOneYear)
				: Instant.now().plusSeconds(expiresInOneDay));
		return refreshTokenRepository.save(refreshToken);
	}


	@Override
	public RefreshTokenResponseBody refreshAccessToken(RefreshTokenRequestBody refreshTokenRequestBody,
	                                                   HttpServletRequest request,
	                                                   HttpServletResponse response) {
		var token = getCookie(request, key);
		var user = userRepository.findById(refreshTokenRequestBody.getUserId())
				.orElseThrow(() -> new BadRequestException("Invalid access token"));

		if (token.isEmpty()) {
			throw new BadRequestException("Refresh token does not exist.");
		}

		var refreshToken = refreshTokenRepository.findByToken(token.get())
				.orElseThrow(() -> new BadRequestException("Token not found."));

		if (!refreshToken.getUser().getId().equals(refreshTokenRequestBody.getUserId())) {
			throw new UnauthorizedException("You do not have permission to refresh access token from another user");
		}

		if (refreshToken.getExpiredAt().isBefore(Instant.now())) {
			deleteRefreshToken(response, refreshToken);
			throw new UnauthorizedException("Refresh token has expired.");
		}

		return mapper.toRefreshTokenResponseBody(refreshToken, user);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		var token = getCookie(request, key);

		if (token.isEmpty()) {
			throw new BadRequestException("Refresh token does not exist.");
		}

		var refreshToken = refreshTokenRepository
				.findByToken(token.get())
				.orElseThrow(() -> new BadRequestException("Token not found."));

		deleteRefreshToken(response, refreshToken);
	}

	private void deleteRefreshToken(HttpServletResponse response, RefreshToken oldRefreshToken) {
		refreshTokenRepository.delete(oldRefreshToken);
		cleanCookie(response, key);
	}

	private void validateRequest(HttpServletRequest request) {
		var cookieRefreshToken = getCookie(request, key);

		if (cookieRefreshToken.isPresent() && !cookieRefreshToken.get().isEmpty()) {
			throw new BadRequestException("User already logged in.");
		}
	}

}
