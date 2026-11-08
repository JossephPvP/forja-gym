package com.forja.gimnasio.service;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Cliente;
import com.forja.gimnasio.model.Membresia;
import com.forja.gimnasio.model.MetodoPago;
import com.forja.gimnasio.model.Pago;
import com.forja.gimnasio.model.Plan;
import com.forja.gimnasio.repository.ClienteRepository;
import com.forja.gimnasio.repository.MembresiaRepository;
import com.forja.gimnasio.repository.PagoRepository;
import com.forja.gimnasio.repository.PlanRepository;
import com.forja.gimnasio.util.Fechas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MembresiaService {

    private final MembresiaRepository membresias;
    private final ClienteRepository clientes;
    private final PlanRepository planes;
    private final PagoRepository pagos;

    public MembresiaService(MembresiaRepository membresias, ClienteRepository clientes,
                            PlanRepository planes, PagoRepository pagos) {
        this.membresias = membresias;
        this.clientes = clientes;
        this.planes = planes;
        this.pagos = pagos;
    }

    public List<Membresia> listar() { return membresias.findAllByOrderByFechaFinDesc(); }

    public List<Membresia> deCliente(Long clienteId) {
        return membresias.findByClienteIdOrderByFechaFinDesc(clienteId);
    }

    public long contarVigentes() { return membresias.contarVigentes(Fechas.hoy()); }

    public List<Membresia> porVencer(int dias) {
        LocalDate hoy = Fechas.hoy();
        return membresias.porVencer(hoy, hoy.plusDays(dias));
    }

    /** Si el cliente aún tiene membresía, la nueva empieza el día siguiente; si no, hoy. */
    public LocalDate inicioSugerido(Long clienteId) {
        LocalDate hoy = Fechas.hoy();
        if (clienteId == null) return hoy;
        return membresias.findFirstByClienteIdAndCanceladaFalseOrderByFechaFinDesc(clienteId)
                .filter(m -> !m.getFechaFin().isBefore(hoy))
                .map(m -> m.getFechaFin().plusDays(1))
                .orElse(hoy);
    }

    /** Caso de uso principal: registrar (o renovar) una membresía y cobrarla. */
    @Transactional
    public Membresia registrar(Long clienteId, Long planId, LocalDate inicio, MetodoPago metodo) {
        if (clienteId == null) throw new ReglaNegocioException("Selecciona un cliente.");
        if (planId == null) throw new ReglaNegocioException("Selecciona un plan.");

        Cliente cliente = clientes.findById(clienteId)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el cliente."));
        if (!cliente.isActivo()) {
            throw new ReglaNegocioException("El cliente está dado de baja.");
        }
        Plan plan = planes.findById(planId)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el plan."));
        if (!plan.isActivo()) {
            throw new ReglaNegocioException("El plan está desactivado.");
        }

        LocalDate desde = (inicio != null) ? inicio : Fechas.hoy();
        LocalDate hasta = desde.plusDays(plan.getDuracionDias() - 1L);

        if (membresias.contarCruces(clienteId, desde, hasta) > 0) {
            throw new ReglaNegocioException(
                    "El cliente ya tiene una membresía en esas fechas. Elige el día en que termina la actual más uno.");
        }

        Membresia m = new Membresia();
        m.setCliente(cliente);
        m.setPlan(plan);
        m.setFechaInicio(desde);
        m.setFechaFin(hasta);
        m.setMonto(plan.getPrecio());
        membresias.save(m);

        Pago pago = new Pago();
        pago.setMembresia(m);
        pago.setMonto(plan.getPrecio());
        pago.setMetodo(metodo != null ? metodo : MetodoPago.EFECTIVO);
        pago.setFechaPago(Fechas.ahora());
        pagos.save(pago);

        return m;
    }

    @Transactional
    public Membresia cancelar(Long id) {
        Membresia m = membresias.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró la membresía."));
        m.setCancelada(true);
        return membresias.save(m);
    }
}
