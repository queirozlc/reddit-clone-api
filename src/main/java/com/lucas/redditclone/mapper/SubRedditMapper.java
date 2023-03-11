package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.subreddit.SubRedditRequestBody;
import com.lucas.redditclone.dto.response.SubRedditResponseBody;
import com.lucas.redditclone.entity.Category;
import com.lucas.redditclone.entity.Post;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface SubRedditMapper {
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "name", source = "subRedditRequestBody.name")
    @Mapping(target = "uri", source = "subRedditRequestBody.uri")
    @Mapping(target = "description", source = "subRedditRequestBody.description")
    SubReddit toSubReddit(SubRedditRequestBody subRedditRequestBody, Category category, User user);


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