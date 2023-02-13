package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.SubReddit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SubRedditMapper {
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", ignore = true)
	@Mapping(target = "posts", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
	@Mapping(source = "userId", target = "user.id")
	SubReddit toSubReddit(SubRedditRequestBody subRedditRequestBody);


	@Mapping(target = "ownerUsername", expression = "java(getOwnerUsername(subReddit))")
	@Mapping(target = "numberOfPosts", expression = "java(mapPosts(subReddit.getPosts()))")
	SubRedditResponseBody toSubRedditResponseBody(SubReddit subReddit);

	default Long mapPosts(List<Post> posts) {
		return (long) posts.size();
	}

	default String getOwnerUsername(SubReddit subReddit) {
		return subReddit.getUser().getUsername();
	}

}