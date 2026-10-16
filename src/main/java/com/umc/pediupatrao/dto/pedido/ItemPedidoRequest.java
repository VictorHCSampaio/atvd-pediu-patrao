package com.umc.pediupatrao.dto.pedido;

public record ItemPedidoRequest(
        String produtoId,
        int quantidade
) {
}
