package com.lucas.redditclone.service.jwt;

import com.lucas.redditclone.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.function.Function;

public interface JwtService {
	String generateToken(Authentication authentication);

	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);

	Claims extractAllClaims(String token);

	Date extractExpirationDate(String token);

	String extractUsername(String token);

	boolean tokenIsExpired(String token);

	boolean validateToken(String token, User user);
}
