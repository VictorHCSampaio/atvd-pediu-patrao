package com.umc.pediupatrao.entity;

import com.umc.pediupatrao.model.AcaoAuditoria;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "auditoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaLog {

    @Id
    private String id;

    private String usuario;
    private String perfil;
    private AcaoAuditoria acao;
    private String entidade;
    private String entidadeId;
    private List<AlteracaoCampo> alteracoes;
    private String justificativa;
    private LocalDateTime dataHora;
}
