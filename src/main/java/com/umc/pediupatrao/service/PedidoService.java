package com.umc.pediupatrao.service;

import com.umc.pediupatrao.config.UsuarioAutenticado;
import com.umc.pediupatrao.dto.pedido.ItemPedidoRequest;
import com.umc.pediupatrao.dto.pedido.PedidoRequest;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.model.StatusPedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(String id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pedido não encontrado."));
    }

    public Pedido criarPedido(PedidoRequest request) {
        if (request == null || request.itens() == null || request.itens().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "O pedido precisa ter ao menos um item.");
        }

        List<ItemPedido> itens = new ArrayList<>();
        for (ItemPedidoRequest itemRequest : request.itens()) {
            if (itemRequest.quantidade() <= 0) {
                throw new ResponseStatusException(BAD_REQUEST, "A quantidade de cada item deve ser maior que zero.");
            }
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Produto inválido no pedido."));

            double precoUnitario = produto.getPreco() == null ? 0.0 : produto.getPreco();
            itens.add(ItemPedido.builder()
                    .produtoId(produto.getId())
                    .nome(produto.getNome())
                    .quantidade(itemRequest.quantidade())
                    .precoUnitario(precoUnitario)
                    .subtotal(precoUnitario * itemRequest.quantidade())
                    .build());
        }

        Pedido pedido = Pedido.builder()
                .clienteId(request.clienteId())
                .itens(itens)
                .descontoPercentual(0.0)
                .valorTotal(calcularTotal(itens, 0.0))
                .status(StatusPedido.RECEBIDO.name())
                .responsavelRegistro(UsuarioAutenticado.exigirUsername())
                .dataHoraEntrada(LocalDateTime.now())
                .build();

        return pedidoRepository.save(pedido);
    }

    public Pedido atualizarStatus(String id, String novoStatusTexto) {
        Pedido pedido = buscarPorId(id);
        StatusPedido atual = statusAtual(pedido);
        StatusPedido destino = converterStatus(novoStatusTexto);

        if (atual.finalizado()) {
            throw new ResponseStatusException(CONFLICT, "Pedido já finalizado ou cancelado não pode ser alterado.");
        }
        if (!atual.podeTransicionarPara(destino)) {
            throw new ResponseStatusException(CONFLICT,
                    "Transição inválida de " + atual + " para " + destino + ".");
        }

        pedido.setStatus(destino.name());
        if (destino.registraSaida()) {
            pedido.setResponsavelSaida(UsuarioAutenticado.exigirUsername());
            pedido.setDataHoraSaida(LocalDateTime.now());
        }

        return pedidoRepository.save(pedido);
    }

    private double calcularTotal(List<ItemPedido> itens, double descontoPercentual) {
        double bruto = itens.stream().mapToDouble(ItemPedido::getSubtotal).sum();
        return bruto * (1 - descontoPercentual / 100.0);
    }

    private StatusPedido statusAtual(Pedido pedido) {
        return converterStatus(pedido.getStatus());
    }

    private StatusPedido converterStatus(String texto) {
        try {
            return StatusPedido.valueOf(texto);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new ResponseStatusException(BAD_REQUEST, "Status de pedido inválido.");
        }
    }
}
