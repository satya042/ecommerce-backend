package com.ecommerce.productservice.service;

import com.ecommerce.productservice.dto.request.ProductRequest;
import com.ecommerce.productservice.dto.response.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Long productId, ProductRequest productRequest);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllProducts();

    Page<ProductResponse> getProducts(int page, int size);

    void deleteProduct(Long productId);
}
