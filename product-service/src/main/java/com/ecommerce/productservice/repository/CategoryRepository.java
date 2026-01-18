package com.ecommerce.productservice.repository;

import com.ecommerce.productservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryTitleIgnoreCase(String categoryTitle);

    boolean existsByCategoryTitleIgnoreCase(String categoryTitle);

    boolean existsByCategoryTitleIgnoreCaseAndCategoryIdNot(String categoryTitle, Long categoryId);
}
