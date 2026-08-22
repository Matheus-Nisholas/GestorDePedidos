package com.nisholas.ordermanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    //id (identificador), name (nome), description (descrição), price (preço), stockQuantity (quantidade em estoque), active (ativo), createdAt (criado em), updatedAt (atualizado em)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    private String description;

    private BigDecimal price;

    private int stockQuantity;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
