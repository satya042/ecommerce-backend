package com.ecommerce.productservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category title cannot be blank")
    private String categoryTitle;

    @Valid
    private ParentCategory parentCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
    public static class ParentCategory {
        @Positive(message = "Parent category id must be greater than zero")
        private Integer parentCategoryId;

        @NotBlank(message = "Parent Category title cannot be blank")
        private String parentCategoryTitle;
    }
}
