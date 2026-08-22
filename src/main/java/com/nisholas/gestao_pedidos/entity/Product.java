package com.nisholas.gestao_pedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Product {

    //id (identificador), name (nome), description (descrição), price (preço), stockQuantity (quantidade em estoque), active (ativo), createdAt (criado em), updatedAt (atualizado em)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    private String description;

    private int price;

    private int stockQuantity;

    private boolean active;

    private String createdAt;

    private String updatedAt;
}
