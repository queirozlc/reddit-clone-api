package com.lucas.redditclone.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.lucas.redditclone.dto.request.post.PostEditRequestBody;
import com.lucas.redditclone.dto.request.post.PostRequestBody;
import com.lucas.redditclone.dto.response.post.PostResponseBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.repository.CommentRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public abstract class PostMapper {

	@Autowired
	CommentRepository commentRepository;

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "voteCount", constant = "0")
	@Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "subReddit", source = "subReddit")
	public abstract Post toPost(PostRequestBody postRequestBody, SubReddit subReddit, User user);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "voteCount", constant = "0")
	@Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "subReddit", source = "subReddit")
	public abstract Post toPost(PostEditRequestBody postEditRequestBody, SubReddit subReddit, User user);

	@Mapping(target = "subRedditName", source = "subReddit.name")
	@Mapping(target = "ownerUsername", source = "user.username")
	@Mapping(target = "numberOfComments", expression = "java(commentCount(post))")
	@Mapping(target = "timeAgo", expression = "java(timeAgo(post))")
	public abstract PostResponseBody toPostResponseBody(Post post);

	Integer commentCount(Post post) {
		return commentRepository.findByPost(post).size();
	}

	String timeAgo(Post post) {
		return TimeAgo.using(post.getCreatedAt().toEpochMilli());
	}
}