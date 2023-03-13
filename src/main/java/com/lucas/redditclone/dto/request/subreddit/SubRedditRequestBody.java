package com.lucas.redditclone.dto.request.subreddit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubRedditRequestBody {
    @NotBlank(message = "Field name is required.")
    private String name;
    @Pattern(regexp = "(?:^| )(r/[a-zA-Z_+]+)", message = "Invalid uri. Try something like that: 'r/memes'.")
    @NotBlank(message = "Field uri is required")
    private String uri;
    @NotBlank(message = "Field description is required.")
    private String description;
    @NotNull(message = "Id of owner user is required.")
    private UUID userId;
    @NotNull(message = "Category Id is required")
    private UUID categoryId;
}
