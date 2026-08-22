package com.nisholas.gestao_pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class ProductOrder {

    //id (identificador), order (pedido), product (produto), quantity (quantidade), unitPrice (preço unitário), subtotal (subtotal)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Order order;

    private Product product;

    private int quantity;

    private int unitPrice;

    private int subtotal;



}
