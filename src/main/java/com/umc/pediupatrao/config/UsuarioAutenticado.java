package com.umc.pediupatrao.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class UsuarioAutenticado {

    private static final String PREFIXO_ROLE = "ROLE_";

    private UsuarioAutenticado() {
    }

    public static String exigirUsername() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null || !autenticacao.isAuthenticated()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Sessão expirada. Entre novamente.");
        }
        return autenticacao.getName();
    }

    public static String perfil() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacao == null) {
            return null;
        }
        return autenticacao.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith(PREFIXO_ROLE))
                .map(authority -> authority.substring(PREFIXO_ROLE.length()))
                .findFirst()
                .orElse(null);
    }
}
