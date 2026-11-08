package com.forja.gimnasio.model;

import com.forja.gimnasio.util.Fechas;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingresa el nombre")
    private String nombre;

    @NotBlank(message = "Ingresa los apellidos")
    private String apellido;

    @NotBlank(message = "Ingresa el DNI")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 dígitos")
    @Column(unique = true, length = 8, nullable = false)
    private String dni;

    private String telefono;

    @Email(message = "Escribe un correo válido")
    private String email;

    private LocalDate fechaRegistro = Fechas.hoy();

    private boolean activo = true;

    @OneToMany(mappedBy = "cliente")
    @OrderBy("fechaFin DESC")
    private List<Membresia> membresias = new ArrayList<>();

    public String getNombreCompleto() { return nombre + " " + apellido; }

    /** Membresía que hoy le permite entrenar, o null si no tiene. */
    public Membresia getMembresiaVigente() {
        for (Membresia m : membresias) {
            EstadoMembresia e = m.getEstado();
            if (e == EstadoMembresia.ACTIVA || e == EstadoMembresia.POR_VENCER) return m;
        }
        return null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public List<Membresia> getMembresias() { return membresias; }
    public void setMembresias(List<Membresia> membresias) { this.membresias = membresias; }
}
