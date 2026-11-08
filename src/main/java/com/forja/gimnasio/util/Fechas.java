package com.forja.gimnasio.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/** Fechas siempre en hora de Lima, sin importar la zona horaria del equipo. */
public final class Fechas {
    private static final ZoneId LIMA = ZoneId.of("America/Lima");

    private Fechas() {}

    public static LocalDate hoy() { return LocalDate.now(LIMA); }

    public static LocalDateTime ahora() { return LocalDateTime.now(LIMA); }
}
