package com.ecommerce.productservice.dto.response;

import com.ecommerce.productservice.model.ProductStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse {
    private UUID productId;
    private String sku;
    private String productTitle;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String brand;
    private ProductStatus productStatus;
    private CategoryResponse category;
}
