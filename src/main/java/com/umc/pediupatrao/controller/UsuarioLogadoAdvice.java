package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.config.UsuarioAutenticado;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UsuarioLogadoAdvice {

    @ModelAttribute("perfilLogado")
    public String perfilLogado() {
        return UsuarioAutenticado.perfil();
    }

    @ModelAttribute("usuarioLogado")
    public String usuarioLogado() {
        try {
            return UsuarioAutenticado.exigirUsername();
        } catch (RuntimeException exception) {
            return null;
        }
    }
}
