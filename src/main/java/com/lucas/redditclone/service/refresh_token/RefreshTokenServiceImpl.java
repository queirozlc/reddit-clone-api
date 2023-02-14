package com.lucas.redditclone.service.refresh_token;

import com.lucas.redditclone.dto.request.refresh_token.RefreshTokenRequestBody;
import com.lucas.redditclone.dto.response.refresh_token.RefreshTokenResponseBody;
import com.lucas.redditclone.entity.RefreshToken;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.mapper.RefreshTokenMapper;
import com.lucas.redditclone.repository.RefreshTokenRepository;
import com.lucas.redditclone.repository.UserRepository;
import com.lucas.redditclone.service.jwt.JwtService;
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
	private final JwtService jwtService;
	@Value("${cookies.expiration.expires-year}")
	private int expiresInOneYear;
	@Value("${cookies.expiration.expires-day}")
	private int expiresInOneDay;
	@Value("${cookies.key.cookie-name}")
	private String key;

	@Override
	public RefreshToken generateRefreshToken(User user,
	                                         boolean rememberMe,
	                                         HttpServletResponse response) {

		if (refreshTokenRepository.existsByUser(user)) {
			throw new BadRequestException("User already have a token");
		}

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
		String token = getCookie(request, key);
		var username = jwtService.extractUsername(refreshTokenRequestBody.getLastAccessToken());
		var user = userRepository.findByUsername(username)
				.orElseThrow(() -> new BadRequestException("Invalid access token"));

		if (token == null || token.isEmpty()) {
			throw new BadRequestException("Refresh token does not exist.");
		}

		var oldRefreshToken = refreshTokenRepository.findByToken(token)
				.orElseThrow(() -> new BadRequestException("Token not found."));

		deleteRefreshToken(response, oldRefreshToken);
		RefreshToken refreshToken = generateRefreshToken(user, refreshTokenRequestBody.isRememberMe(), response);
		return mapper.toRefreshTokenResponseBody(refreshToken, user);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response) {
		String token = getCookie(request, key);

		if (token == null || token.isEmpty()) {
			throw new BadRequestException("Refresh token does not exist.");
		}
		var refreshToken = refreshTokenRepository
				.findByToken(token)
				.orElseThrow(() -> new BadRequestException("Token not found."));

		deleteRefreshToken(response, refreshToken);
	}

	private void deleteRefreshToken(HttpServletResponse response, RefreshToken oldRefreshToken) {
		cleanCookie(response, key);
		refreshTokenRepository.delete(oldRefreshToken);
	}
}
