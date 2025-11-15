package com.ecommerce.productservice.mapper;

import com.ecommerce.productservice.dto.response.ProductResponse;
import com.ecommerce.productservice.model.Product;

public final class ProductMappingHelper {

    private ProductMappingHelper() {
    }

    public static ProductResponse productToResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setProductId(product.getProductId());
        response.setSku(product.getSku());
        response.setProductTitle(product.getProductTitle());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setBrand(product.getBrand());
        response.setProductStatus(product.getProductStatus());
        response.setCategory(CategoryMappingHelper.categoryToResponse(product.getCategory()));
        return response;
    }
}
