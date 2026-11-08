package com.forja.gimnasio.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Registro de cada vez que un cliente se presenta en recepción. */
@Entity
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    private LocalDateTime fechaHora;

    /** true si tenía membresía vigente y pudo ingresar. */
    private boolean permitido;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public boolean isPermitido() { return permitido; }
    public void setPermitido(boolean permitido) { this.permitido = permitido; }
}
