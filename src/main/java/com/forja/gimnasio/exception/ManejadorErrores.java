package com.forja.gimnasio.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ReglaNegocioException.class)
    public String reglaNegocio(ReglaNegocioException e, Model model, HttpServletResponse respuesta) {
        respuesta.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
