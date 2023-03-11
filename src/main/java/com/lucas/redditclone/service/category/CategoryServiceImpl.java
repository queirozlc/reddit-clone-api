package com.lucas.redditclone.service.category;

import com.lucas.redditclone.dto.request.category.CategoryRequestBody;
import com.lucas.redditclone.dto.response.category.CategoryResponseBody;
import com.lucas.redditclone.entity.Category;
import com.lucas.redditclone.exception.bad_request.BadRequestException;
import com.lucas.redditclone.mapper.CategoryMapper;
import com.lucas.redditclone.repository.CategoryRepository;
import com.lucas.redditclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    public static final String CATEGORY_NOT_FOUND = "Category not found.";
    public static final String USER_NOT_FOUND = "User not found.";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;
    private final UserRepository userRepository;

    @Override
    public CategoryResponseBody createCategory(CategoryRequestBody categoryRequestBody) {
        var category = mapCategory(categoryRequestBody);

        if (category.getParent().getId() != null) {
            var categoryParent = categoryRepository.findById(category.getParent().getId())
                    .orElseThrow(() -> new BadRequestException(CATEGORY_NOT_FOUND));
            category.setParent(categoryParent);
        } else {
            category.setParent(null);
        }

        if (categoryRepository.existsByNameOrUri(category.getName(), category.getUri())) {
            throw new BadRequestException("Category already exists.");
        }

        var categorySaved = categoryRepository.save(category);
        return mapper.toCategoryResponseBody(categorySaved);
    }

    @Override
    public CategoryResponseBody updateCategory(CategoryRequestBody categoryRequestBody, UUID id) {
        var categoryToBeUpdated = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(CATEGORY_NOT_FOUND));
        var categoryRequest = mapCategory(categoryRequestBody);

        if (categoryRequest.getParent().getId() != null) {
            var parentCategory = categoryRepository.findById(categoryRequest.getParent().getId())
                    .orElseThrow(() -> new BadRequestException(CATEGORY_NOT_FOUND));
            categoryRequest.setParent(parentCategory);
        } else {
            categoryRequest.setParent(categoryToBeUpdated.getParent());
        }

        if (!categoryRequest.getName().equals(categoryToBeUpdated.getName()) ||
                !categoryRequest.getUri().equals(categoryToBeUpdated.getUri()) &&
                        categoryRepository.existsByNameOrUri(categoryRequest.getName(), categoryRequest.getUri())) {
            throw new BadRequestException("Category already exists.");
        }

        categoryRequest.setId(categoryToBeUpdated.getId());
        Category categoryEdited = categoryRepository.save(categoryRequest);
        return mapper.toCategoryResponseBody(categoryEdited);
    }

    @Override
    public Page<CategoryResponseBody> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(mapper::toCategoryResponseBody);
    }

    @Override
    public void deleteCategory(UUID id) {
        var categoryToDelete = categoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(CATEGORY_NOT_FOUND));

        categoryRepository.delete(categoryToDelete);
    }

    private Category mapCategory(CategoryRequestBody categoryRequestBody) {
        var user = userRepository.findById(categoryRequestBody.getUserId())
                .orElseThrow(() -> new BadRequestException(USER_NOT_FOUND));
        return mapper.toCategory(categoryRequestBody, user);
    }
}
