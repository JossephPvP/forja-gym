package com.forja.gimnasio.model;

public enum MetodoPago {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    YAPE("Yape"),
    PLIN("Plin"),
    TRANSFERENCIA("Transferencia");

    private final String label;

    MetodoPago(String label) { this.label = label; }

    public String getLabel() { return label; }
}
