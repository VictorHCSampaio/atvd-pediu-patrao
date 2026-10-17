package com.umc.pediupatrao.repository;

import com.umc.pediupatrao.entity.AuditoriaLog;
import com.umc.pediupatrao.model.AcaoAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditoriaLogRepository extends MongoRepository<AuditoriaLog, String> {

    Page<AuditoriaLog> findByEntidade(String entidade, Pageable pageable);

    Page<AuditoriaLog> findByAcao(AcaoAuditoria acao, Pageable pageable);

    Page<AuditoriaLog> findByEntidadeAndAcao(String entidade, AcaoAuditoria acao, Pageable pageable);
}
