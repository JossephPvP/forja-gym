package com.forja.gimnasio.service;

import java.io.Serializable;

/** Resultado de pasar el DNI en recepción (datos simples para mostrar en pantalla). */
public class ResultadoIngreso implements Serializable {

    private final Long clienteId;
    private final String nombre;
    private final boolean permitido;
    private final String mensaje;

    public ResultadoIngreso(Long clienteId, String nombre, boolean permitido, String mensaje) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.permitido = permitido;
        this.mensaje = mensaje;
    }

    public Long getClienteId() { return clienteId; }
    public String getNombre() { return nombre; }
    public boolean isPermitido() { return permitido; }
    public String getMensaje() { return mensaje; }
}
