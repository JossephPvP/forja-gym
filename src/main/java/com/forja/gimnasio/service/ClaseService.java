package com.forja.gimnasio.service;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Clase;
import com.forja.gimnasio.model.Cliente;
import com.forja.gimnasio.model.Reserva;
import com.forja.gimnasio.repository.ClaseRepository;
import com.forja.gimnasio.repository.ClienteRepository;
import com.forja.gimnasio.repository.MembresiaRepository;
import com.forja.gimnasio.repository.ReservaRepository;
import com.forja.gimnasio.util.Fechas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClaseService {

    private final ClaseRepository clases;
    private final ReservaRepository reservas;
    private final ClienteRepository clientes;
    private final MembresiaRepository membresias;

    public ClaseService(ClaseRepository clases, ReservaRepository reservas,
                        ClienteRepository clientes, MembresiaRepository membresias) {
        this.clases = clases;
        this.reservas = reservas;
        this.clientes = clientes;
        this.membresias = membresias;
    }

    public List<Clase> proximas() {
        return clases.findByFechaHoraGreaterThanEqualOrderByFechaHora(Fechas.hoy().atStartOfDay());
    }

    public Clase obtener(Long id) {
        return clases.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró la clase."));
    }

    public Clase guardar(Clase clase) { return clases.save(clase); }

    public List<Reserva> reservasActivas(Long claseId) {
        return reservas.findByClaseIdAndCanceladaFalseOrderByFechaReservaAsc(claseId);
    }

    @Transactional
    public Reserva reservar(Long claseId, String dni) {
        Clase clase = obtener(claseId);
        if (clase.getFechaHora().isBefore(Fechas.ahora())) {
            throw new ReglaNegocioException("La clase ya pasó.");
        }
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new ReglaNegocioException("Escribe un DNI de 8 dígitos.");
        }
        Cliente cliente = clientes.findByDni(dni)
                .orElseThrow(() -> new ReglaNegocioException("No existe un cliente con el DNI " + dni + "."));
        if (!cliente.isActivo()) {
            throw new ReglaNegocioException("El cliente está dado de baja.");
        }

        LocalDate dia = clase.getFechaHora().toLocalDate();
        if (membresias.vigentesDe(cliente.getId(), dia).isEmpty()) {
            throw new ReglaNegocioException("El cliente no tiene membresía vigente el día de la clase.");
        }
        if (reservas.existsByClienteIdAndClaseIdAndCanceladaFalse(cliente.getId(), claseId)) {
            throw new ReglaNegocioException("El cliente ya tiene una reserva en esta clase.");
        }
        if (reservas.countByClaseIdAndCanceladaFalse(claseId) >= clase.getCupo()) {
            throw new ReglaNegocioException("No quedan cupos en esta clase.");
        }

        Reserva r = new Reserva();
        r.setCliente(cliente);
        r.setClase(clase);
        r.setFechaReserva(Fechas.ahora());
        return reservas.save(r);
    }

    @Transactional
    public void cancelarReserva(Long reservaId) {
        Reserva r = reservas.findById(reservaId)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró la reserva."));
        r.setCancelada(true);
        reservas.save(r);
    }
}
