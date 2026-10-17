package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.service.AuditoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuditoriaViewController {

    private final AuditoriaService auditoriaService;

    public AuditoriaViewController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/auditoria")
    public String auditoria(Model model) {
        model.addAttribute("registros", auditoriaService.listar(null, null, 0, 200));
        model.addAttribute("content", "auditoria/lista :: content");
        return "layout";
    }
}
