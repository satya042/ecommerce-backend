package com.ecommerce.orderservice.request;

import com.ecommerce.orderservice.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {

    private OrderStatus status;
    private String paymentMethod;
    private String paymentStatus;
    private BigDecimal totalAmount;
}
