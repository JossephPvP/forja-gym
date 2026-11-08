package com.forja.gimnasio.exception;

/** Error esperado por una regla del negocio (DNI repetido, sin cupos, etc.). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) { super(mensaje); }
}
