package com.ecommerce.productservice.service.impl;

import com.ecommerce.productservice.Exception.CategoryNotFoundException;
import com.ecommerce.productservice.Exception.DuplicateResourceException;
import com.ecommerce.productservice.Exception.ProductNotFoundException;
import com.ecommerce.productservice.dto.request.ProductRequest;
import com.ecommerce.productservice.dto.response.ProductResponse;
import com.ecommerce.productservice.mapper.ProductMappingHelper;
import com.ecommerce.productservice.model.Category;
import com.ecommerce.productservice.model.Product;
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
	public ProductResponse getProductById(UUID productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
		return ProductMappingHelper.productToResponse(product);
	}

	@Override
	public ProductResponse createProduct(ProductRequest productRequest) {
		String normalizedSku = normalize(productRequest.getSku());
		if (productRepository.existsBySkuIgnoreCase(normalizedSku)) {
			throw new DuplicateResourceException("Product already exists with sku: " + normalizedSku);
		}

		Product product = new Product();
		applyRequestToProduct(productRequest, product);
		product.setSku(normalizedSku);
		Product savedProduct = productRepository.save(product);
		log.info("Product {} is saved", savedProduct.getProductId());
		return ProductMappingHelper.productToResponse(savedProduct);
	}

	@Override
	public ProductResponse updateProduct(UUID productId, ProductRequest productRequest) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

		String normalizedSku = normalize(productRequest.getSku());
		if (productRepository.existsBySkuIgnoreCaseAndProductIdNot(normalizedSku, productId)) {
			throw new DuplicateResourceException("Another product already exists with sku: " + normalizedSku);
		}

		applyRequestToProduct(productRequest, product);
		product.setSku(normalizedSku);
		Product updatedProduct = productRepository.save(product);
		log.info("Product {} is updated", updatedProduct.getProductId());
		return ProductMappingHelper.productToResponse(updatedProduct);
	}

	@Override
	public void deleteProduct(UUID productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));
		productRepository.delete(product);
		log.info("Product {} is deleted", productId);
	}

	private void applyRequestToProduct(ProductRequest request, Product product) {
		product.setProductTitle(normalize(request.getProductTitle()));
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setQuantity(request.getQuantity());
		product.setBrand(normalize(request.getBrand()));
		product.setProductStatus(request.getProductStatus());
		product.setCategory(resolveCategory(request.getCategoryId()));
	}

	private Category resolveCategory(Integer categoryId) {
		return categoryRepository.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + categoryId));
	}

	private String normalize(String value) {
		return value == null ? null : value.trim();
	}
}
