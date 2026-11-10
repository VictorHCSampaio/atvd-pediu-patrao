package com.umc.pediupatrao.dto.dashboard;

public record VendasPorPeriodoResponse(
        String periodo,
        long quantidadePedidos,
        double valorTotal
) {}
