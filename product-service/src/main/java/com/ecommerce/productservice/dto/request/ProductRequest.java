package com.ecommerce.productservice.dto.request;

import com.ecommerce.productservice.entity.ProductStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {

	@NotBlank(message = "Product title cannot be blank")
	private String productTitle;

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;

	@NotNull(message = "Price cannot be null")
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
	private BigDecimal price;

	@NotNull(message = "Quantity cannot be null")
	@Min(value = 0, message = "Quantity must be greater than or equal to zero")
	private  Integer quantity;

	@NotBlank(message = "Brand cannot be blank")
	private String brand;

	@NotNull(message = "Product status is required")
	private ProductStatus productStatus;

	@NotNull(message = "Category id is required")
	private Long categoryId;
}
