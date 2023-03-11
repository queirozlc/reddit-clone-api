package com.lucas.redditclone.controller;

import com.lucas.redditclone.dto.request.category.CategoryRequestBody;
import com.lucas.redditclone.dto.response.category.CategoryResponseBody;
import com.lucas.redditclone.service.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseBody> createCategory(@RequestBody @Valid CategoryRequestBody categoryRequestbody) {
        return new ResponseEntity<>(categoryService.createCategory(categoryRequestbody), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CategoryResponseBody>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryResponseBody> updateCategory(@PathVariable UUID id, @RequestBody @Valid CategoryRequestBody categoryRequestbody) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryRequestbody, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully.");
    }
}
