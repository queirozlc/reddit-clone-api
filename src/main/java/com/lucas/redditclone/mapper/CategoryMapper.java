package com.lucas.redditclone.mapper;

import com.lucas.redditclone.dto.request.category.CategoryRequestBody;
import com.lucas.redditclone.dto.response.category.CategoryResponseBody;
import com.lucas.redditclone.entity.Category;
import com.lucas.redditclone.entity.SubReddit;
import com.lucas.redditclone.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "subReddits", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "childrenCategories", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "parent.id", source = "categoryRequestBody.categoryParentId")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "name", source = "categoryRequestBody.name")
    Category toCategory(CategoryRequestBody categoryRequestBody, User user);


    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "categoryParentId", source = "parent.id")
    CategoryRequestBody toCategoryRequestBody(Category category);


    @Mapping(target = "numberOfSubreddits", expression = "java(getNumberOfSubreddits(category))")
    @Mapping(target = "numberOfPosts", expression = "java(getNumberOfPosts(category))")
    CategoryResponseBody toCategoryResponseBody(Category category);

    default Long getNumberOfPosts(Category category) {
        if (category.getSubReddits() != null && !category.getSubReddits().isEmpty()) {
            return category.getSubReddits().stream().map(SubReddit::getPosts).map(List::size).count();
        }
        return 0L;
    }

    default Long getNumberOfSubreddits(Category category) {
        if (category.getSubReddits() != null && !category.getSubReddits().isEmpty()) {
            return (long) category.getSubReddits().size();
        }
        return 0L;
    }
}