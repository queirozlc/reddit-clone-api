package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.response.refresh_token.RefreshTokenResponseBody;
import com.lucas.redditclone.entity.RefreshToken;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.service.jwt.JwtService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class RefreshTokenMapper {

	@Autowired
	JwtService jwtService;

	@Mapping(target = "user", source = "user")
	@Mapping(target = "token", expression = "java(java.util.UUID.randomUUID().toString())")
	@Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "expiredAt", ignore = true)
	public abstract RefreshToken toRefreshToken(User user);

	@Mapping(target = "newAccessToken", expression = "java(generateJwtAccessToken(user))")
	@Mapping(target = "username", source = "user.username")
	public abstract RefreshTokenResponseBody toRefreshTokenResponseBody(RefreshToken refreshToken, User user);

	String generateJwtAccessToken(User user) {
		return jwtService.generateToken(user);
	}
}