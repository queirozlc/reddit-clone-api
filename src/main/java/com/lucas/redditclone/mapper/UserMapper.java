package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.user.UserRequest;
import com.lucas.redditclone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	User toUser(UserRequest userRequest);
}