package com.umc.pediupatrao.dto.dashboard;

public record VendasPorTipoProdutoResponse(
        String tipo,
        long quantidadeVendida,
        double faturamento
) {}
