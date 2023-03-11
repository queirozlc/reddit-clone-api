package com.lucas.redditclone.service.category;

import com.lucas.redditclone.dto.request.category.CategoryRequestBody;
import com.lucas.redditclone.dto.response.category.CategoryResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CategoryService {

    CategoryResponseBody createCategory(CategoryRequestBody categoryRequestBody);

    CategoryResponseBody updateCategory(CategoryRequestBody categoryRequestBody, UUID id);

    Page<CategoryResponseBody> getAllCategories(Pageable pageable);

    void deleteCategory(UUID id);
}
