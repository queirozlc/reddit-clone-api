package com.lucas.redditclone.dto.response.category;

import com.lucas.redditclone.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryResponseBody {
    private String name;
    private String uri;
    private String description;
    private Long numberOfSubreddits;
    private Long numberOfPosts;
    private List<Category> childrenCategories;
}
