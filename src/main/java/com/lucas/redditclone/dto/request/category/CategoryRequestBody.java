package com.lucas.redditclone.dto.request.category;

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
public class CategoryRequestBody {

    @NotBlank(message = "Category name is required.")
    private String name;
    @NotBlank(message = "Category uri is required.")
    @Pattern(regexp = "(?:^| )(t/[a-zA-Z_+]+)", message = "Invalid uri. Try something like that: 't/gaming'.")
    private String uri;
    private String description;
    private UUID categoryParentId;
    @NotNull(message = "Id for user owner of category is required.")
    private UUID userId;
}
