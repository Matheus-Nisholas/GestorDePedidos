package com.nisholas.gestao_pedidos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "client")
@Getter
@Setter
public class Client {

    //id (identificador), name (nome), email (e-mail), phone (telefone), active (ativo), createdAt (criado em), updatedAt (atualizado em)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 11)
    private int phone;

    private boolean active;

    private String createdAt;

    private String updatedAt;



}
