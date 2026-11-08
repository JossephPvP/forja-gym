package com.forja.gimnasio.controller;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Clase;
import com.forja.gimnasio.service.ClaseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clases")
public class ClaseController {

    private final ClaseService service;

    public ClaseController(ClaseService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clases", service.proximas());
        return "clases/lista";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("clase", new Clase());
        return "clases/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("clase") Clase clase,
                          BindingResult resultado, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "clases/form";
        }
        Clase guardada = service.guardar(clase);
        flash.addFlashAttribute("mensaje", "Clase creada.");
        return "redirect:/clases/" + guardada.getId();
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("clase", service.obtener(id));
        model.addAttribute("reservas", service.reservasActivas(id));
        return "clases/detalle";
    }

    @PostMapping("/{id}/reservas")
    public String reservar(@PathVariable Long id, @RequestParam String dni, RedirectAttributes flash) {
        try {
            service.reservar(id, dni.trim());
            flash.addFlashAttribute("mensaje", "Reserva confirmada.");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/clases/" + id;
    }

    @PostMapping("/{claseId}/reservas/{reservaId}/cancelar")
    public String cancelarReserva(@PathVariable Long claseId, @PathVariable Long reservaId,
                                  RedirectAttributes flash) {
        service.cancelarReserva(reservaId);
        flash.addFlashAttribute("mensaje", "Reserva cancelada.");
        return "redirect:/clases/" + claseId;
    }
}
