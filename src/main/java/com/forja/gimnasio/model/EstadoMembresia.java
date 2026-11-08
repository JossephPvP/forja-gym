package com.forja.gimnasio.model;

public enum EstadoMembresia {
    ACTIVA("Activa", "ok"),
    POR_VENCER("Por vencer", "warn"),
    PROGRAMADA("Programada", "info"),
    VENCIDA("Vencida", "bad"),
    CANCELADA("Cancelada", "muted");

    private final String label;
    private final String css;

    EstadoMembresia(String label, String css) {
        this.label = label;
        this.css = css;
    }

    public String getLabel() { return label; }
    public String getCss() { return css; }
}
