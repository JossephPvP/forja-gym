package com.forja.gimnasio.service;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Asistencia;
import com.forja.gimnasio.model.Cliente;
import com.forja.gimnasio.model.Membresia;
import com.forja.gimnasio.repository.AsistenciaRepository;
import com.forja.gimnasio.repository.ClienteRepository;
import com.forja.gimnasio.repository.MembresiaRepository;
import com.forja.gimnasio.util.Fechas;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AsistenciaService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AsistenciaRepository asistencias;
    private final ClienteRepository clientes;
    private final MembresiaRepository membresias;

    public AsistenciaService(AsistenciaRepository asistencias, ClienteRepository clientes,
                             MembresiaRepository membresias) {
        this.asistencias = asistencias;
        this.clientes = clientes;
        this.membresias = membresias;
    }

    public List<Asistencia> delDia() {
        LocalDate hoy = Fechas.hoy();
        return asistencias.findByFechaHoraBetweenOrderByFechaHoraDesc(
                hoy.atStartOfDay(), hoy.atTime(LocalTime.MAX));
    }

    /** Caso de uso de recepción: el cliente da su DNI y se valida su membresía. */
    @Transactional
    public ResultadoIngreso registrarIngreso(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new ReglaNegocioException("Escribe un DNI de 8 dígitos.");
        }
        Cliente cliente = clientes.findByDni(dni)
                .orElseThrow(() -> new ReglaNegocioException(
                        "No existe un cliente con el DNI " + dni + ". Regístralo en Clientes."));

        boolean permitido = false;
        String mensaje;

        if (!cliente.isActivo()) {
            mensaje = "El cliente está dado de baja.";
        } else {
            List<Membresia> vigentes = membresias.vigentesDe(cliente.getId(), Fechas.hoy());
            if (vigentes.isEmpty()) {
                mensaje = "No tiene membresía vigente. Ofrécele renovar.";
            } else {
                permitido = true;
                Membresia m = vigentes.get(0);
                mensaje = "Plan " + m.getPlan().getNombre() + ", vigente hasta el "
                        + m.getFechaFin().format(FORMATO);
                if (m.getDiasRestantes() <= 7) {
                    mensaje += ". Le quedan " + m.getDiasRestantes() + " días: ofrécele renovar.";
                }
            }
        }

        Asistencia registro = new Asistencia();
        registro.setCliente(cliente);
        registro.setFechaHora(Fechas.ahora());
        registro.setPermitido(permitido);
        asistencias.save(registro);

        return new ResultadoIngreso(cliente.getId(), cliente.getNombreCompleto(), permitido, mensaje);
    }
}
