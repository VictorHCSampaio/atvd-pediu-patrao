package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.dto.auditoria.AuditoriaLogResponse;
import com.umc.pediupatrao.model.AcaoAuditoria;
import com.umc.pediupatrao.service.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaLogResponse>> listar(@RequestParam(required = false) String entidade,
                                                            @RequestParam(required = false) AcaoAuditoria acao,
                                                            @RequestParam(defaultValue = "0") int pagina,
                                                            @RequestParam(defaultValue = "50") int tamanho) {
        return ResponseEntity.ok(auditoriaService.listar(entidade, acao, pagina, tamanho));
    }
}
