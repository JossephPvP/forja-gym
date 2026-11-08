package com.forja.gimnasio.model;

import com.forja.gimnasio.util.Fechas;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Membresia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    private Plan plan;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @Column(precision = 10, scale = 2)
    private BigDecimal monto;

    private boolean cancelada = false;

    @OneToMany(mappedBy = "membresia")
    private List<Pago> pagos = new ArrayList<>();

    public EstadoMembresia getEstado() {
        LocalDate hoy = Fechas.hoy();
        if (cancelada) return EstadoMembresia.CANCELADA;
        if (hoy.isAfter(fechaFin)) return EstadoMembresia.VENCIDA;
        if (hoy.isBefore(fechaInicio)) return EstadoMembresia.PROGRAMADA;
        if (!hoy.plusDays(7).isBefore(fechaFin)) return EstadoMembresia.POR_VENCER;
        return EstadoMembresia.ACTIVA;
    }

    public long getDiasRestantes() {
        return ChronoUnit.DAYS.between(Fechas.hoy(), fechaFin);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public boolean isCancelada() { return cancelada; }
    public void setCancelada(boolean cancelada) { this.cancelada = cancelada; }
    public List<Pago> getPagos() { return pagos; }
    public void setPagos(List<Pago> pagos) { this.pagos = pagos; }
}
