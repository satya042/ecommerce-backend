package com.ecommerce.productservice.dto.response;

import com.ecommerce.productservice.entity.ProductStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class ProductResponse {
    private Long productId;
    private String sku;
    private String productTitle;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String brand;
    private ProductStatus productStatus;
    private CategoryResponse category;
}
