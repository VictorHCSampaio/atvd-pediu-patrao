package com.umc.pediupatrao.dto.auditoria;

import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.model.AcaoAuditoria;

import java.time.LocalDateTime;
import java.util.List;

public record AuditoriaLogResponse(
        String id,
        String usuario,
        String perfil,
        AcaoAuditoria acao,
        String entidade,
        String entidadeId,
        List<AlteracaoCampo> alteracoes,
        String justificativa,
        LocalDateTime dataHora
) {
}
