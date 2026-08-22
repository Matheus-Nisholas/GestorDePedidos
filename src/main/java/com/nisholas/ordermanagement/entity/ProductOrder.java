package com.nisholas.ordermanagement.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class ProductOrder {

    //id (identificador), order (pedido), product (produto), quantity (quantidade), unitPrice (preço unitário), subtotal (subtotal)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;



}
