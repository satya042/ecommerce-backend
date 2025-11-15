package com.ecommerce.productservice.mapper;

import com.ecommerce.productservice.dto.response.CategoryResponse;
import com.ecommerce.productservice.model.Category;

public interface CategoryMappingHelper {

    static CategoryResponse categoryToResponse(Category category) {
        if (category == null) {
            return null;
        }
        return CategoryResponse.builder().
                categoryId(category.getCategoryId()).
                categoryTitle(category.getCategoryTitle()).
                parentCategory(categoryToResponse(category.getParentCategory())).
                build();
    }
}
