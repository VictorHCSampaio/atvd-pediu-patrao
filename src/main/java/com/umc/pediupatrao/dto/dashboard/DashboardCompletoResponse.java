package com.umc.pediupatrao.dto.dashboard;

import java.util.List;

public record DashboardCompletoResponse(
        IndicadoresGeraisResponse indicadores,
        List<VendasPorPeriodoResponse> vendasPorPeriodo,
        List<StatusPedidoResponse> situacaoPedidos,
        List<ProdutoMaisVendidoResponse> produtosMaisVendidos,
        List<VendasPorTipoProdutoResponse> vendasPorTipo
) {}
