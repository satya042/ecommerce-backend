package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.request.CategoryRequest;
import com.ecommerce.productservice.dto.response.CategoryResponse;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();

    Page<CategoryResponse> getAllCategoriesWithPaged(int page, int size);

    CategoryResponse getCategoryById(int categoryId);

    CategoryResponse createCategory(@Valid CategoryRequest categoryRequest);

    CategoryResponse updateCategory(int categoryId, @Valid CategoryRequest categoryRequest);

    void deleteCategory(int categoryId);
}
