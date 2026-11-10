package com.umc.pediupatrao.dto.dashboard;

public record IndicadoresGeraisResponse(
        long totalPedidos,
        long pedidosFinalizados,
        long pedidosCancelados,
        long pedidosEmAndamento,
        double faturamentoTotal,
        double ticketMedio,
        double taxaCancelamento
) {}
