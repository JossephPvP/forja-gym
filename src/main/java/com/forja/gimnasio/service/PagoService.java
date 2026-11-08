package com.forja.gimnasio.service;

import com.forja.gimnasio.model.Pago;
import com.forja.gimnasio.repository.PagoRepository;
import com.forja.gimnasio.util.Fechas;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagos;

    public PagoService(PagoRepository pagos) {
        this.pagos = pagos;
    }

    public List<Pago> listar() { return pagos.findAllByOrderByFechaPagoDesc(); }

    public List<Pago> deCliente(Long clienteId) {
        return pagos.findByMembresiaClienteIdOrderByFechaPagoDesc(clienteId);
    }

    public BigDecimal ingresosDelMes() {
        LocalDate hoy = Fechas.hoy();
        LocalDateTime desde = hoy.withDayOfMonth(1).atStartOfDay();
        LocalDateTime hasta = hoy.withDayOfMonth(1).plusMonths(1).atStartOfDay();
        BigDecimal total = pagos.totalEntre(desde, hasta);
        return total != null ? total : BigDecimal.ZERO;
    }
}
