package com.umc.pediupatrao.service;

import com.umc.pediupatrao.dto.dashboard.*;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.model.StatusPedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public DashboardService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }


    public IndicadoresGeraisResponse indicadoresGerais(LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos = filtrarPorPeriodo(inicio, fim);

        long total = pedidos.size();
        long finalizados = pedidos.stream()
                .filter(p -> StatusPedido.FINALIZADO.name().equals(p.getStatus()))
                .count();
        long cancelados = pedidos.stream()
                .filter(p -> StatusPedido.CANCELADO.name().equals(p.getStatus()))
                .count();
        long emAndamento = total - finalizados - cancelados;

        double faturamento = pedidos.stream()
                .filter(p -> StatusPedido.FINALIZADO.name().equals(p.getStatus()))
                .mapToDouble(Pedido::getValorTotal)
                .sum();

        double ticketMedio = finalizados > 0 ? faturamento / finalizados : 0.0;
        double taxaCancelamento = total > 0 ? (cancelados * 100.0) / total : 0.0;

        return new IndicadoresGeraisResponse(
                total, finalizados, cancelados, emAndamento,
                arredondar(faturamento), arredondar(ticketMedio), arredondar(taxaCancelamento)
        );
    }


    public List<VendasPorPeriodoResponse> vendasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos = filtrarPorPeriodo(inicio, fim).stream()
                .filter(p -> StatusPedido.FINALIZADO.name().equals(p.getStatus()))
                .toList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Map<String, List<Pedido>> agrupado = pedidos.stream()
                .filter(p -> p.getDataHoraEntrada() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getDataHoraEntrada().toLocalDate().format(formatter),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        return agrupado.entrySet().stream()
                .map(entry -> new VendasPorPeriodoResponse(
                        entry.getKey(),
                        entry.getValue().size(),
                        arredondar(entry.getValue().stream().mapToDouble(Pedido::getValorTotal).sum())
                ))
                .toList();
    }


    public List<StatusPedidoResponse> situacaoPedidos(LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos = filtrarPorPeriodo(inicio, fim);

        Map<String, Long> contagem = pedidos.stream()
                .collect(Collectors.groupingBy(Pedido::getStatus, Collectors.counting()));


        return Arrays.stream(StatusPedido.values())
                .map(s -> new StatusPedidoResponse(s.name(), contagem.getOrDefault(s.name(), 0L)))
                .toList();
    }


    public List<ProdutoMaisVendidoResponse> produtosMaisVendidos(LocalDateTime inicio, LocalDateTime fim, int limite) {
        List<Pedido> pedidos = filtrarPorPeriodo(inicio, fim).stream()
                .filter(p -> !StatusPedido.CANCELADO.name().equals(p.getStatus()))
                .toList();


        Map<String, Produto> produtosMap = produtoRepository.findAll().stream()
                .collect(Collectors.toMap(Produto::getId, p -> p, (a, b) ->  // Agrupa itens por produtoId
        Map<String, List<ItemPedido>> itensPorProduto = pedidos.stream()
                .filter(p -> p.getItens() != null)
                .flatMap(p -> p.getItens().stream())
                .collect(Collectors.groupingBy(ItemPedido::getProdutoId));

        return itensPorProduto.entrySet().stream()
                .map(entry -> {
                    String produtoId = entry.getKey();
                    List<ItemPedido> itens = entry.getValue();
                    long qtd = itens.stream().mapToLong(ItemPedido::getQuantidade).sum();
                    double fat = itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
                    String nome = Optional.ofNullable(produtosMap.get(produtoId))
                            .map(Produto::getNome)
                            .orElse(itens.isEmpty() ? produtoId : itens.get(0).getNome());
                    return new ProdutoMaisVendidoResponse(produtoId, nome, qtd, arredondar(fat));
                })
                .sorted(Comparator.comparingLong(ProdutoMaisVendidoResponse::quantidadeVendida).reversed())
                .limit(limite)
                .toList();
    }


    public List<VendasPorTipoProdutoResponse> vendasPorTipoProduto(LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> pedidos = filtrarPorPeriodo(inicio, fim).stream()
                .filter(p -> !StatusPedido.CANCELADO.name().equals(p.getStatus()))
                .toList();

        Map<String, Produto> produtosMap = produtoRepository.findAll().stream()
                .collect(Collectors.toMap(Produto::getId, p -> p, (a, b) -> a));

        Map<String, List<ItemPedido>> itensPorTipo = pedidos.stream()
                .filter(p -> p.getItens() != null)
                .flatMap(p -> p.getItens().stream())
                .collect(Collectors.groupingBy(item -> {
                    Produto produto = produtosMap.get(item.getProdutoId());
                    return produto != null && produto.getTipo() != null ? produto.getTipo() : "Outros";
                }));

        return itensPorTipo.entrySet().stream()
                .map(entry -> new VendasPorTipoProdutoResponse(
                        entry.getKey(),
                        entry.getValue().stream().mapToLong(ItemPedido::getQuantidade).sum(),
                        arredondar(entry.getValue().stream().mapToDouble(ItemPedido::getSubtotal).sum())
                ))
                .sorted(Comparator.comparingDouble(VendasPorTipoProdutoResponse::faturamento).reversed())
                .toList();
    }



    private List<Pedido> filtrarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        List<Pedido> todos = pedidoRepository.findAll();
        if (inicio == null && fim == null) {
            return todos;
        }
        return todos.stream()
                .filter(p -> p.getDataHoraEntrada() != null)
                .filter(p -> inicio == null || !p.getDataHoraEntrada().isBefore(inicio))
                .filter(p -> fim == null || !p.getDataHoraEntrada().isAfter(fim))
                .toList();
    }

    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
