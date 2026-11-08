package com.forja.gimnasio.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingresa el nombre de la clase")
    private String nombre;

    @NotBlank(message = "Ingresa el instructor")
    private String instructor;

    @NotNull(message = "Elige la fecha y la hora")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fechaHora;

    @Min(value = 1, message = "El cupo mínimo es 1")
    private int cupo = 15;

    @OneToMany(mappedBy = "clase")
    private List<Reserva> reservas = new ArrayList<>();

    public int getCuposDisponibles() {
        long ocupados = reservas.stream().filter(r -> !r.isCancelada()).count();
        return (int) (cupo - ocupados);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public int getCupo() { return cupo; }
    public void setCupo(int cupo) { this.cupo = cupo; }
    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
}
