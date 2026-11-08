package com.forja.gimnasio.controller;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.service.AsistenciaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AsistenciaController {

    private final AsistenciaService service;

    public AsistenciaController(AsistenciaService service) {
        this.service = service;
    }

    @GetMapping("/asistencia")
    public String listar(Model model) {
        model.addAttribute("asistencias", service.delDia());
        return "asistencia/lista";
    }

    @PostMapping("/asistencia/ingreso")
    public String registrarIngreso(@RequestParam String dni, RedirectAttributes flash) {
        try {
            flash.addFlashAttribute("resultado", service.registrarIngreso(dni.trim()));
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asistencia";
    }
}
