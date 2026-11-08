package com.forja.gimnasio.controller;

import com.forja.gimnasio.model.Plan;
import com.forja.gimnasio.service.PlanService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/planes")
public class PlanController {

    private final PlanService service;

    public PlanController(PlanService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("planes", service.listar());
        return "planes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("plan", new Plan());
        return "planes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("plan", service.obtener(id));
        return "planes/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("plan") Plan plan,
                          BindingResult resultado, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "planes/form";
        }
        service.guardar(plan);
        flash.addFlashAttribute("mensaje", "Plan guardado.");
        return "redirect:/planes";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id, RedirectAttributes flash) {
        service.cambiarEstado(id);
        flash.addFlashAttribute("mensaje", "Plan actualizado.");
        return "redirect:/planes";
    }
}
