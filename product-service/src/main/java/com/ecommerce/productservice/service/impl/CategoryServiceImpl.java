package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.Exception.CategoryNotFoundException;
import com.ecommerce.productservice.Exception.CategoryOperationException;
import com.ecommerce.productservice.Exception.DuplicateResourceException;
import com.ecommerce.productservice.dto.request.CategoryRequest;
import com.ecommerce.productservice.dto.response.CategoryResponse;
import com.ecommerce.productservice.mapper.CategoryMappingHelper;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream() 
                .map(CategoryMappingHelper::categoryToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategoriesWithPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoryPage = categoryRepository.findAll(pageable);
        List<CategoryResponse> categoryResponseList = categoryPage.getContent().stream()
                .map(CategoryMappingHelper::categoryToResponse)
                .toList();
        return new PageImpl<>(categoryResponseList, pageable, categoryPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
        return CategoryMappingHelper.categoryToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        String categoryTitle = categoryRequest.getCategoryTitle().trim();
        Category parentCategory = null;
        if (categoryRepository.existsByCategoryTitleIgnoreCase(categoryTitle)) {
            throw new DuplicateResourceException("Category already exists with title: " + categoryTitle);
        }
        if(categoryRequest.getParentCategory() != null){
            parentCategory = validateParentCategory(categoryRequest.getParentCategory().getParentCategoryId(), null);
        }
        Category category = Category.builder()
                .categoryTitle(categoryTitle)
                .parentCategory(parentCategory)
                .build();
        return CategoryMappingHelper.categoryToResponse(categoryRepository.save(category));
    }
    
    
    @Override
    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        String categoryTitle = categoryRequest.getCategoryTitle().trim();
        Category parentCategory = null;
        Category existingCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        if (categoryRepository.existsByCategoryTitleIgnoreCaseAndCategoryIdNot(categoryTitle, categoryId)) {
            throw new DuplicateResourceException("Another category already exists with title: " + categoryTitle);
        }
        if(categoryRequest.getParentCategory() != null){
            parentCategory = validateParentCategory(categoryRequest.getParentCategory().getParentCategoryId(), categoryId);
        }
        existingCategory.setCategoryTitle(categoryTitle);
        existingCategory.setParentCategory(parentCategory);
        return CategoryMappingHelper.categoryToResponse(categoryRepository.save(existingCategory));
    }


    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));

        if (category.getSubCategories() != null && !category.getSubCategories().isEmpty()) {
            throw new CategoryOperationException("Cannot delete category with existing sub-categories");
        }

        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new CategoryOperationException("Cannot delete category with associated products");
        }
        categoryRepository.delete(category);
    }

    private Category validateParentCategory(Long parentCategoryId, Long currentCategoryId) {
        if (parentCategoryId == null) {
            return null;
        }

        Category parentCategory = categoryRepository.findById(parentCategoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Parent category not found with id: " + parentCategoryId));

        if (currentCategoryId != null && parentCategoryId.equals(currentCategoryId)) {
            throw new CategoryOperationException("Category cannot be its own parent");
        }

        if (currentCategoryId != null) {
            Category cursor = parentCategory;
            while (cursor != null) {
                if (cursor.getCategoryId() != null && cursor.getCategoryId().equals(currentCategoryId)) {
                    throw new CategoryOperationException("Cannot set a child category as parent");
                }
                cursor = cursor.getParentCategory();
            }
        }
        return parentCategory;
    }
}
