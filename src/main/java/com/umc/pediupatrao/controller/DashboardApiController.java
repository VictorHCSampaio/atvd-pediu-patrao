package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.dashboard.*;
import com.umc.pediupatrao.service.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardService dashboardService;

    public DashboardApiController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // Endpoint único que retorna todos os dados do dashboard
    @GetMapping("/completo")
    public DashboardCompletoResponse dashboardCompleto(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        LocalDateTime inicio = toInicio(dataInicio);
        LocalDateTime fim = toFim(dataFim);
        return new DashboardCompletoResponse(
                dashboardService.indicadoresGerais(inicio, fim),
                dashboardService.vendasPorPeriodo(inicio, fim),
                dashboardService.situacaoPedidos(inicio, fim),
                dashboardService.produtosMaisVendidos(inicio, fim, 10),
                dashboardService.vendasPorTipoProduto(inicio, fim)
        );
    }

    // RF01 — Indicadores gerais de vendas
    @GetMapping("/indicadores")
    public IndicadoresGeraisResponse indicadores(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.indicadoresGerais(toInicio(dataInicio), toFim(dataFim));
    }

    // RF02 — Análise de vendas (gráfico de vendas por dia)
    @GetMapping("/vendas-por-periodo")
    public List<VendasPorPeriodoResponse> vendasPorPeriodo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.vendasPorPeriodo(toInicio(dataInicio), toFim(dataFim));
    }

    // RF03 — Situação dos pedidos (gráfico de pizza por status)
    @GetMapping("/situacao-pedidos")
    public List<StatusPedidoResponse> situacaoPedidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.situacaoPedidos(toInicio(dataInicio), toFim(dataFim));
    }

    // RF04 — Produtos mais vendidos (ranking)
    @GetMapping("/produtos-mais-vendidos")
    public List<ProdutoMaisVendidoResponse> produtosMaisVendidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "10") int limite) {
        return dashboardService.produtosMaisVendidos(toInicio(dataInicio), toFim(dataFim), limite);
    }

    // Vendas agrupadas por tipo de produto
    @GetMapping("/vendas-por-tipo")
    public List<VendasPorTipoProdutoResponse> vendasPorTipo(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return dashboardService.vendasPorTipoProduto(toInicio(dataInicio), toFim(dataFim));
    }

    // --- helpers para converter LocalDate em LocalDateTime ---

    private LocalDateTime toInicio(LocalDate data) {
        return data != null ? data.atStartOfDay() : null;
    }

    private LocalDateTime toFim(LocalDate data) {
        return data != null ? data.atTime(23, 59, 59) : null;
    }
}
