package com.ecommerce.orderservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderLineItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal pricePerUnit;
    private Integer quantity;
    private BigDecimal totalPrice;
}
