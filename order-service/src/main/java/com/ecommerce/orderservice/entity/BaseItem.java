package com.ecommerce.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseItem {
    @Column(name = "product_id", nullable = false)
    protected Long productId;

    @Column(name = "product_name", nullable = false)
    protected String productName;

    @Column(name = "sku_code")
    protected String skuCode;

    @Column(name = "price_per_unit", nullable = false)
    protected BigDecimal pricePerUnit;

    @Column(name = "quantity", nullable = false)
    protected Integer quantity;

    @Column(name = "total_price", nullable = false)
    protected BigDecimal totalPrice;
}
