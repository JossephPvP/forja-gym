package com.forja.gimnasio.controller;

import com.forja.gimnasio.service.PagoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping("/pagos")
    public String listar(Model model) {
        model.addAttribute("pagos", service.listar());
        model.addAttribute("totalMes", service.ingresosDelMes());
        return "pagos/lista";
    }
}
