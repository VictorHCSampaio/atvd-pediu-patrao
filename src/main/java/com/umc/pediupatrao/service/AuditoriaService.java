package com.umc.pediupatrao.service;

import com.umc.pediupatrao.config.UsuarioAutenticado;
import com.umc.pediupatrao.dto.auditoria.AuditoriaLogResponse;
import com.umc.pediupatrao.entity.AlteracaoCampo;
import com.umc.pediupatrao.entity.AuditoriaLog;
import com.umc.pediupatrao.model.AcaoAuditoria;
import com.umc.pediupatrao.repository.AuditoriaLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.FORBIDDEN;

@Service
public class AuditoriaService {

    private static final Logger logger = LoggerFactory.getLogger(AuditoriaService.class);
    private static final int TAMANHO_MAXIMO_PAGINA = 200;
    // Somente estes perfis podem consultar a auditoria.
    private static final Set<String> PERFIS_CONSULTA = Set.of("ADMIN", "GERENTE");

    private final AuditoriaLogRepository auditoriaLogRepository;

    public AuditoriaService(AuditoriaLogRepository auditoriaLogRepository) {
        this.auditoriaLogRepository = auditoriaLogRepository;
    }

    // Registra um evento de auditoria. A falha ao auditar nunca pode interromper a
    // operação de negócio, por isso é tratada e apenas logada.
    public void registrar(AcaoAuditoria acao, String entidade, String entidadeId,
                          List<AlteracaoCampo> alteracoes, String justificativa) {
        try {
            AuditoriaLog registro = AuditoriaLog.builder()
                    .usuario(UsuarioAutenticado.exigirUsername())
                    .perfil(UsuarioAutenticado.perfil())
                    .acao(acao)
                    .entidade(entidade)
                    .entidadeId(entidadeId)
                    .alteracoes(alteracoes)
                    .justificativa(justificativa)
                    .dataHora(LocalDateTime.now())
                    .build();

            auditoriaLogRepository.save(registro);
            logger.info("Auditoria registrada. acao={} entidade={} entidadeId={}", acao, entidade, entidadeId);
        } catch (RuntimeException exception) {
            logger.error("Falha ao registrar auditoria. acao={} entidade={}", acao, entidade, exception);
        }
    }

    public void registrar(AcaoAuditoria acao, String entidade, String entidadeId, List<AlteracaoCampo> alteracoes) {
        registrar(acao, entidade, entidadeId, alteracoes, null);
    }

    public List<AuditoriaLogResponse> listar(String entidade, AcaoAuditoria acao, int pagina, int tamanho) {
        String perfil = UsuarioAutenticado.perfil();
        if (perfil == null || !PERFIS_CONSULTA.contains(perfil)) {
            throw new ResponseStatusException(FORBIDDEN, "Apenas ADMIN e GERENTE podem consultar a auditoria.");
        }

        Pageable pageable = PageRequest.of(
                Math.max(pagina, 0),
                Math.min(Math.max(tamanho, 1), TAMANHO_MAXIMO_PAGINA),
                Sort.by(Sort.Direction.DESC, "dataHora")
        );

        Page<AuditoriaLog> resultado;
        if (entidade != null && acao != null) {
            resultado = auditoriaLogRepository.findByEntidadeAndAcao(entidade, acao, pageable);
        } else if (entidade != null) {
            resultado = auditoriaLogRepository.findByEntidade(entidade, pageable);
        } else if (acao != null) {
            resultado = auditoriaLogRepository.findByAcao(acao, pageable);
        } else {
            resultado = auditoriaLogRepository.findAll(pageable);
        }

        return resultado.getContent().stream()
                .map(this::paraResponse)
                .toList();
    }

    private AuditoriaLogResponse paraResponse(AuditoriaLog registro) {
        return new AuditoriaLogResponse(
                registro.getId(),
                registro.getUsuario(),
                registro.getPerfil(),
                registro.getAcao(),
                registro.getEntidade(),
                registro.getEntidadeId(),
                registro.getAlteracoes(),
                registro.getJustificativa(),
                registro.getDataHora()
        );
    }
}
