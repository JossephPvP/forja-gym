package com.forja.gimnasio.controller;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Membresia;
import com.forja.gimnasio.model.MetodoPago;
import com.forja.gimnasio.service.ClienteService;
import com.forja.gimnasio.service.MembresiaService;
import com.forja.gimnasio.service.PlanService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/membresias")
public class MembresiaController {

    private final MembresiaService membresias;
    private final ClienteService clientes;
    private final PlanService planes;

    public MembresiaController(MembresiaService membresias, ClienteService clientes, PlanService planes) {
        this.membresias = membresias;
        this.clientes = clientes;
        this.planes = planes;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("membresias", membresias.listar());
        return "membresias/lista";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam(required = false) Long clienteId, Model model) {
        model.addAttribute("clientes", clientes.listarActivos());
        model.addAttribute("planes", planes.listarActivos());
        model.addAttribute("metodos", MetodoPago.values());
        model.addAttribute("clienteId", clienteId);
        model.addAttribute("inicio", membresias.inicioSugerido(clienteId));
        return "membresias/form";
    }

    @PostMapping
    public String registrar(@RequestParam Long clienteId,
                            @RequestParam Long planId,
                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
                            @RequestParam MetodoPago metodo,
                            RedirectAttributes flash) {
        try {
            Membresia m = membresias.registrar(clienteId, planId, fechaInicio, metodo);
            flash.addFlashAttribute("mensaje", "Membresía registrada y pago cobrado.");
            return "redirect:/clientes/" + m.getCliente().getId();
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/membresias/nueva?clienteId=" + clienteId;
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes flash) {
        Membresia m = membresias.cancelar(id);
        flash.addFlashAttribute("mensaje", "Membresía cancelada.");
        return "redirect:/clientes/" + m.getCliente().getId();
    }
}
