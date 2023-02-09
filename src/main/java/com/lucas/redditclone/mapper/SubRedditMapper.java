package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.SubReddit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SubRedditMapper {
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "posts", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(source = "userId", target = "user.id")
	SubReddit toSubReddit(SubRedditRequestBody subRedditRequestBody);


	@Mapping(source = "user", target = "owner")
	SubRedditResponseBody toSubRedditResponseBody(SubReddit subReddit);
}