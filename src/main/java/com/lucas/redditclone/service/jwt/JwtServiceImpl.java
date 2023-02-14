package com.lucas.redditclone.service.jwt;

import com.lucas.redditclone.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
	@Value("${jwt.token.secret-key}")
	public String secretKey;

	@Override
	public String generateToken(User user) {
		return Jwts
				.builder()
				.setSubject(user.getUsername())
				.claim("id", user.getId())
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.setExpiration(Date.from(Instant.now().plusSeconds(300)))
				.setIssuedAt(new Date())
				.compact();
	}

	@Override
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		var claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	@Override
	public Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}


	@Override
	public Date extractExpirationDate(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	@Override
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	@Override
	public boolean tokenIsExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}

	@Override
	public boolean validateToken(String token, User user) {
		var username = extractUsername(token);
		return (username.equals(user.getUsername()) && !tokenIsExpired(token));
	}

	private Key getSignKey() {
		byte[] keys = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keys);
	}
}
