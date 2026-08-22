package com.nisholas.ordermanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    //id (identificador), name (nome), email (e-mail), phone (telefone), active (ativo), createdAt (criado em), updatedAt (atualizado em)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column()
    private String email;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 11)
    private int phone;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



}
