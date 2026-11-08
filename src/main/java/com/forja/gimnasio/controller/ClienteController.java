package com.forja.gimnasio.controller;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Cliente;
import com.forja.gimnasio.service.ClienteService;
import com.forja.gimnasio.service.MembresiaService;
import com.forja.gimnasio.service.PagoService;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clientes;
    private final MembresiaService membresias;
    private final PagoService pagos;

    public ClienteController(ClienteService clientes, MembresiaService membresias, PagoService pagos) {
        this.clientes = clientes;
        this.membresias = membresias;
        this.pagos = pagos;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("clientes", clientes.listar(q));
        model.addAttribute("q", q);
        return "clientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "clientes/form";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clientes.obtener(id));
        model.addAttribute("membresias", membresias.deCliente(id));
        model.addAttribute("pagos", pagos.deCliente(id));
        return "clientes/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("cliente", clientes.obtener(id));
        return "clientes/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente,
                          BindingResult resultado, RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "clientes/form";
        }
        try {
            Cliente guardado = clientes.guardar(cliente);
            flash.addFlashAttribute("mensaje", "Cliente guardado.");
            return "redirect:/clientes/" + guardado.getId();
        } catch (ReglaNegocioException e) {
            resultado.rejectValue("dni", "dni.duplicado", e.getMessage());
            return "clientes/form";
        }
    }

    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes flash) {
        clientes.darDeBaja(id);
        flash.addFlashAttribute("mensaje", "Cliente dado de baja.");
        return "redirect:/clientes";
    }
}
