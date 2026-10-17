package com.umc.pediupatrao.service;

import com.umc.pediupatrao.config.UsuarioAutenticado;
import com.umc.pediupatrao.dto.pedido.ItemPedidoRequest;
import com.umc.pediupatrao.dto.pedido.PedidoRequest;
import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.ItemPedido;
import com.umc.pediupatrao.entity.Pedido;
import com.umc.pediupatrao.entity.Produto;
import com.umc.pediupatrao.model.AcaoAuditoria;
import com.umc.pediupatrao.model.StatusPedido;
import com.umc.pediupatrao.repository.PedidoRepository;
import com.umc.pediupatrao.repository.ProdutoRepository;
import org.springframework.security.access.prepost.PreAuthorize;
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

    private static final String ENTIDADE = "Pedido";
    private static final double DESCONTO_MAXIMO = 20.0;

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final AuditoriaService auditoriaService;

    public PedidoService(PedidoRepository pedidoRepository,
                         ProdutoRepository produtoRepository,
                         AuditoriaService auditoriaService) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.auditoriaService = auditoriaService;
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

        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar(AcaoAuditoria.PEDIDO_CRIADO, ENTIDADE, salvo.getId(),
                List.of(new AlteracaoCampo("status", null, StatusPedido.RECEBIDO.name())));
        return salvo;
    }

    // Aplica uma transição de status, validando o fluxo e registrando entrada/saída.
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

        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar(AcaoAuditoria.PEDIDO_STATUS_ALTERADO, ENTIDADE, salvo.getId(),
                List.of(new AlteracaoCampo("status", atual.name(), destino.name())));
        return salvo;
    }

    // Desconto exclusivo do gerente, percentual, limitado a 20% e somente antes da saída.
    @PreAuthorize("hasRole('GERENTE')")
    public Pedido aplicarDesconto(String id, double percentual) {
        Pedido pedido = buscarPorId(id);
        StatusPedido atual = statusAtual(pedido);

        if (!atual.antesDaSaida()) {
            throw new ResponseStatusException(CONFLICT, "Desconto só pode ser aplicado antes da saída do pedido.");
        }
        if (percentual <= 0 || percentual > DESCONTO_MAXIMO) {
            throw new ResponseStatusException(BAD_REQUEST, "O desconto deve ser maior que zero e no máximo 20%.");
        }

        double descontoAnterior = pedido.getDescontoPercentual();
        double totalAnterior = pedido.getValorTotal();

        pedido.setDescontoPercentual(percentual);
        pedido.setValorTotal(calcularTotal(pedido.getItens(), percentual));

        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar(AcaoAuditoria.PEDIDO_DESCONTO_APLICADO, ENTIDADE, salvo.getId(), List.of(
                new AlteracaoCampo("descontoPercentual", String.valueOf(descontoAnterior), String.valueOf(percentual)),
                new AlteracaoCampo("valorTotal", String.valueOf(totalAnterior), String.valueOf(salvo.getValorTotal()))
        ));
        return salvo;
    }

    // Cancelamento exclusivo do gerente, com justificativa e somente antes da saída.
    // Preserva os dados históricos e impede qualquer alteração posterior.
    @PreAuthorize("hasRole('GERENTE')")
    public Pedido cancelar(String id, String justificativa) {
        Pedido pedido = buscarPorId(id);
        StatusPedido atual = statusAtual(pedido);

        if (justificativa == null || justificativa.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "O cancelamento exige uma justificativa.");
        }
        if (atual.finalizado()) {
            throw new ResponseStatusException(CONFLICT, "Pedido já finalizado ou cancelado não pode ser cancelado.");
        }
        if (!atual.antesDaSaida()) {
            throw new ResponseStatusException(CONFLICT, "Pedido que já saiu para entrega ou foi retirado não pode ser cancelado.");
        }

        pedido.setStatus(StatusPedido.CANCELADO.name());
        pedido.setJustificativaCancelamento(justificativa);

        Pedido salvo = pedidoRepository.save(pedido);
        auditoriaService.registrar(AcaoAuditoria.PEDIDO_CANCELADO, ENTIDADE, salvo.getId(),
                List.of(new AlteracaoCampo("status", atual.name(), StatusPedido.CANCELADO.name())), justificativa);
        return salvo;
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
