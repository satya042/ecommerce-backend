package com.ecommerce.orderservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_item")
public class CartItem extends BaseItem{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
}
