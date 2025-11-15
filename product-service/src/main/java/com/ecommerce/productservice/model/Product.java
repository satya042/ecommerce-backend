package com.ecommerce.productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private UUID productId;

	@Column(unique = true)
	private String sku;

	@Column(name = "product_title")
	private String productTitle;

	@Column(length = 500)
	private String description;

	@Column(precision = 10, scale = 2)
	private BigDecimal price;

	private  Integer quantity;

	@Column(length = 100)
	private String brand;

	@Column(name = "product_status")
	private ProductStatus productStatus;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id")
	private Category category;
}
