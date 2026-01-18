package com.ecommerce.productservice.mapper;

import com.ecommerce.productservice.dto.response.ProductResponse;
import com.ecommerce.productservice.entity.Product;

public final class ProductMappingHelper {

    public static ProductResponse productToResponse(Product product) {
        if (product == null) {
            return null;
        }
        return ProductResponse.builder()
        .productId(product.getProductId())
        .sku(product.getSku())
        .productTitle(product.getProductTitle())
        .description(product.getDescription())
        .price(product.getPrice())
        .quantity(product.getQuantity())
        .brand(product.getBrand())
        .productStatus(product.getProductStatus())
        .category(CategoryMappingHelper.categoryToResponse(product.getCategory()))
        .build();
    }
}
