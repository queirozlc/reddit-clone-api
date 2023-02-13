package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.post.PostEditRequestBody;
import com.lucas.redditclone.dto.request.post.PostRequestBody;
import com.lucas.redditclone.dto.response.post.PostResponseBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PostMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "voteCount", expression = "java(0)")
	@Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "subReddit", source = "subReddit")
	Post toPost(PostRequestBody postRequestBody, SubReddit subReddit, User user);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "voteCount", expression = "java(0)")
	@Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "subReddit", source = "subReddit")
	Post toPost(PostEditRequestBody postEditRequestBody, SubReddit subReddit, User user);

	@Mapping(target = "subRedditName", source = "subReddit.name")
	@Mapping(target = "ownerUsername", source = "user.username")
	PostResponseBody toPostResponseBody(Post post);

}