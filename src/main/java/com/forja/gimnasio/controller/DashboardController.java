package com.forja.gimnasio.controller;

import com.forja.gimnasio.service.AsistenciaService;
import com.forja.gimnasio.service.ClienteService;
import com.forja.gimnasio.service.DashboardService;
import com.forja.gimnasio.service.MembresiaService;
import com.forja.gimnasio.service.PagoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final ClienteService clientes;
    private final MembresiaService membresias;
    private final PagoService pagos;
    private final AsistenciaService asistencias;
    private final DashboardService dashboard;

    public DashboardController(ClienteService clientes, MembresiaService membresias,
                               PagoService pagos, AsistenciaService asistencias,
                               DashboardService dashboard) {
        this.clientes = clientes;
        this.membresias = membresias;
        this.pagos = pagos;
        this.asistencias = asistencias;
        this.dashboard = dashboard;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalClientes", clientes.contarActivos());
        model.addAttribute("vigentes", membresias.contarVigentes());
        model.addAttribute("porVencer", membresias.porVencer(7));
        model.addAttribute("ingresosMes", pagos.ingresosDelMes());
        model.addAttribute("asistenciasHoy",
        asistencias.delDia().stream().filter(a -> a.isPermitido()).count());
        model.addAttribute("graficos", dashboard.graficos());
        return "dashboard";
    }
}
