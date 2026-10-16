package com.umc.pediupatrao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedido {

    private String produtoId;
    private String nome;
    private int quantidade;
    private double precoUnitario;
    private double subtotal;
}
