package com.ecommerce.productservice.controller;

import com.ecommerce.productservice.dto.request.CategoryRequest;
import com.ecommerce.productservice.dto.response.CategoryResponse;
import com.ecommerce.productservice.service.CategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(){

        log.info("Fetching all categories");

        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/paging")
    public ResponseEntity<Page<CategoryResponse>> getAllCategoriesPaged(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page index must be zero or positive") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "Page size must be greater than zero") int size){

        log.info("Fetching categories with paging: page={}, size={}", page, size);

        Page<CategoryResponse> categoryPage = categoryService.getAllCategoriesWithPaged(page, size);
        return ResponseEntity.ok().body(categoryPage);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategory(
            @PathVariable("categoryId") @Positive(message = "Category id must be greater than zero") final Long categoryId) {
        log.info("Fetching category with id={}", categoryId);
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest categoryRequest){
        log.info("Creating a new category");
        CategoryResponse response = categoryService.createCategory(categoryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> update(
            @PathVariable("categoryId") @Positive(message = "Category id must be greater than zero") final Long categoryId,
            @RequestBody @Valid CategoryRequest categoryRequest){
        log.info("Updating category with category id = {} ", categoryId);
        CategoryResponse updatedCategory = categoryService.updateCategory(categoryId, categoryRequest);
        return ResponseEntity.ok().body(updatedCategory);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(
            @PathVariable("categoryId") @Positive(message = "Category id must be greater than zero") final Long categoryId){

        log.info("Deleting category with category id = {} ", categoryId);

        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
