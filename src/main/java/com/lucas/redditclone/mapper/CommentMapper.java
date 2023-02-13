package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.comment.CommentRequestBody;
import com.lucas.redditclone.dto.response.comment.CommentResponseBody;
import com.lucas.redditclone.entity.Comment;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CommentMapper {

	@Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
	@Mapping(target = "body", source = "commentRequestBody.body")
	@Mapping(target = "post", source = "post")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "id", ignore = true)
	Comment toComment(CommentRequestBody commentRequestBody, Post post, User user);

	@Mapping(target = "postTitle", source = "comment.post.title")
	@Mapping(target = "parentUsername", source = "comment.user.username")
	@Mapping(target = "subRedditName", source = "comment.post.subReddit.name")
	CommentResponseBody toCommentResponseBody(Comment comment);
}