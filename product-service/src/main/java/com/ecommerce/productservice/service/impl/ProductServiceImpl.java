package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.Exception.CategoryNotFoundException;
import com.ecommerce.productservice.Exception.ProductNotFoundException;
import com.ecommerce.productservice.dto.request.ProductRequest;
import com.ecommerce.productservice.dto.response.ProductResponse;
import com.ecommerce.productservice.mapper.ProductMappingHelper;
import com.ecommerce.productservice.entity.Category;
import com.ecommerce.productservice.entity.Product;
import com.ecommerce.productservice.repository.CategoryRepository;
import com.ecommerce.productservice.repository.ProductRepository;
import com.ecommerce.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts() {
		return productRepository.findAll().stream()
				.map(ProductMappingHelper::productToResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ProductResponse> getProducts(int page, int size) {
		PageRequest pageable = PageRequest.of(page, size);
		return productRepository.findAll(pageable)
				.map(ProductMappingHelper::productToResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getProductById(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
		return ProductMappingHelper.productToResponse(product);
	}

	@Override
	public ProductResponse createProduct(ProductRequest productRequest) {
		String prefix = productRequest.getProductTitle().substring(0, Math.min(3,  productRequest.getProductTitle().length())).toUpperCase().replaceAll("[^A-Z]", "");
		String random = UUID.randomUUID().toString().substring(0,6).toUpperCase();
		String productSku =  prefix + "-" + random;

		Product product = applyRequestToProduct(productRequest);
		product.setSku(productSku);
		Product savedProduct = productRepository.save(product);
		log.info("Product {} is saved", savedProduct.getProductId());
		return ProductMappingHelper.productToResponse(savedProduct);
	}

	@Override
	public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		Product updatedProduct = applyRequestToProduct(productRequest);
		updatedProduct.setProductId(productId);
		updatedProduct.setSku(product.getSku());
		Product saveProduct = productRepository.save(updatedProduct);
		log.info("Product {} is updated", saveProduct.getProductId());
		return ProductMappingHelper.productToResponse(saveProduct);
	}

	@Override
	public void deleteProduct(Long productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
		productRepository.delete(product);
		log.info("Product {} is deleted", productId);
	}

	private Product applyRequestToProduct(ProductRequest request) {
        if (request == null) {
            return null;
        }
        return Product.builder()
        .productTitle(request.getProductTitle().trim())
        .description(request.getDescription().trim())
        .price(request.getPrice())
		.quantity(request.getQuantity())
        .brand(request.getBrand().trim())
        .productStatus(request.getProductStatus())
		.category(resolveCategory(request.getCategoryId()))
        .build();
	}

	private Category resolveCategory(Long categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
	}
}
