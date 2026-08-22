package com.nisholas.gestao_pedidos.entity;

import jakarta.persistence.*;

public class Order {

    //id (identificador), customer (cliente), status (situação), totalAmount (valor total), createdAt (criado em), updatedAt (atualizado em)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @OneToMany
    private Customer customer;

    private boolean status;

    private int totalAmount;

    private String createdAt;

    private String updatedAt;
}
