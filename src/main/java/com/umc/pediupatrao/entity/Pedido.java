package com.umc.pediupatrao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    private String id;
    private String clienteId;

    private List<ItemPedido> itens;
    private double descontoPercentual;
    private double valorTotal;
    private String status;
    private String responsavelRegistro;
    private LocalDateTime dataHoraEntrada;
    private String responsavelSaida;
    private LocalDateTime dataHoraSaida;
    private String justificativaCancelamento;
    private String tipoPizza;
    private String sabor;
    private int quantidade;
}
