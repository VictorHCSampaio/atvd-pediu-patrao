package com.umc.pediupatrao.dto.dashboard;

public record ProdutoMaisVendidoResponse(
        String produtoId,
        String nome,
        long quantidadeVendida,
        double faturamento
) {}
