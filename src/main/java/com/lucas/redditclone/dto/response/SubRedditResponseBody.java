package com.lucas.redditclone.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubRedditResponseBody {
    private String name;
    private String description;
    private String uri;
    private String ownerUsername;
    private Long numberOfPosts;
}
