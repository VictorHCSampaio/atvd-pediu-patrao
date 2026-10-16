package com.umc.pediupatrao.model;

import java.util.Set;

public enum StatusPedido {

    RECEBIDO,
    EM_PREPARACAO,
    PRONTO,
    SAIU_PARA_ENTREGA,
    RETIRADO,
    FINALIZADO,
    CANCELADO;

    public Set<StatusPedido> transicoesPermitidas() {
        return switch (this) {
            case RECEBIDO -> Set.of(EM_PREPARACAO, CANCELADO);
            case EM_PREPARACAO -> Set.of(PRONTO, CANCELADO);
            case PRONTO -> Set.of(SAIU_PARA_ENTREGA, RETIRADO, CANCELADO);
            case SAIU_PARA_ENTREGA, RETIRADO -> Set.of(FINALIZADO);
            case FINALIZADO, CANCELADO -> Set.of();
        };
    }

    public boolean podeTransicionarPara(StatusPedido destino) {
        return transicoesPermitidas().contains(destino);
    }

    public boolean antesDaSaida() {
        return this == RECEBIDO || this == EM_PREPARACAO || this == PRONTO;
    }

    public boolean registraSaida() {
        return this == SAIU_PARA_ENTREGA || this == RETIRADO;
    }

    public boolean finalizado() {
        return this == FINALIZADO || this == CANCELADO;
    }
}