package com.umc.pediupatrao.dto.pedido;

import java.util.List;

public record PedidoRequest(
        String clienteId,
        List<ItemPedidoRequest> itens
) {
}
