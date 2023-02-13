package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.vote.VoteRequestBody;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.User;
import com.lucas.redditclone.entity.Vote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface VoteMapper {

	@Mapping(target = "post", source = "post")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "id", ignore = true)
	Vote toVote(VoteRequestBody voteRequestBody, Post post, User user);
}