package com.umc.pediupatrao.config;

import com.umc.pediupatrao.model.AcaoAuditoria;
import com.umc.pediupatrao.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class AcessoNegadoHandler implements AccessDeniedHandler {

    private final AuditoriaService auditoriaService;

    public AcessoNegadoHandler(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        auditoriaService.registrar(AcaoAuditoria.ACESSO_NEGADO, "Autorizacao",
                request.getMethod() + " " + request.getRequestURI(), List.of());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
    }
}
