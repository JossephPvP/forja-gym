package com.forja.gimnasio.config;

import com.forja.gimnasio.model.Clase;
import com.forja.gimnasio.model.Cliente;
import com.forja.gimnasio.model.MetodoPago;
import com.forja.gimnasio.model.Plan;
import com.forja.gimnasio.repository.ClaseRepository;
import com.forja.gimnasio.repository.ClienteRepository;
import com.forja.gimnasio.repository.PlanRepository;
import com.forja.gimnasio.service.ClienteService;
import com.forja.gimnasio.service.MembresiaService;
import com.forja.gimnasio.util.Fechas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Carga planes y datos de ejemplo la primera vez que se levanta el sistema. */
@Component
public class DatosIniciales implements CommandLineRunner {

    private final PlanRepository planes;
    private final ClienteRepository clientes;
    private final ClaseRepository clases;
    private final ClienteService clienteService;
    private final MembresiaService membresiaService;
    private final boolean datosDemo;

    public DatosIniciales(PlanRepository planes, ClienteRepository clientes, ClaseRepository clases,
                          ClienteService clienteService, MembresiaService membresiaService,
                          @Value("${forja.datos-demo:true}") boolean datosDemo) {
        this.planes = planes;
        this.clientes = clientes;
        this.clases = clases;
        this.clienteService = clienteService;
        this.membresiaService = membresiaService;
        this.datosDemo = datosDemo;
    }

    @Override
    public void run(String... args) {
        if (planes.count() == 0) {
            crearPlan("Pase diario", 1, "10.00", "Acceso por un día");
            crearPlan("Mensual", 30, "90.00", "Acceso libre a sala y clases");
            crearPlan("Trimestral", 90, "240.00", "Tres meses con un descuento");
            crearPlan("Semestral", 180, "420.00", "Seis meses con un descuento");
            crearPlan("Anual", 365, "720.00", "Un año completo al mejor precio");
        }

        if (datosDemo && clientes.count() == 0) {
            LocalDate hoy = Fechas.hoy();
            Plan mensual = planes.findAll().stream()
                    .filter(p -> p.getNombre().equals("Mensual"))
                    .findFirst().orElseThrow();

            Cliente lucia = crearCliente("Lucía", "Quispe Rojas", "70123456", "987654321", "lucia@example.com");
            Cliente diego = crearCliente("Diego", "Paredes Soto", "70234567", "986543210", "diego@example.com");
            crearCliente("Camila", "Vargas Ríos", "70345678", "985432109", null);

            // Lucía: vigente. Diego: vence en 3 días. Camila: sin membresía.
            membresiaService.registrar(lucia.getId(), mensual.getId(), hoy.minusDays(10), MetodoPago.YAPE);
            membresiaService.registrar(diego.getId(), mensual.getId(), hoy.minusDays(26), MetodoPago.EFECTIVO);
        }

        if (datosDemo && clases.count() == 0) {
            LocalDate manana = Fechas.hoy().plusDays(1);
            crearClase("Spinning", "Marco Salazar", manana.atTime(7, 0), 15);
            crearClase("Funcional", "Andrea Huamán", manana.atTime(18, 30), 12);
            crearClase("Yoga", "Rosa Medina", manana.plusDays(1).atTime(19, 0), 10);
        }
    }

    private Plan crearPlan(String nombre, int dias, String precio, String descripcion) {
        Plan p = new Plan();
        p.setNombre(nombre);
        p.setDuracionDias(dias);
        p.setPrecio(new BigDecimal(precio));
        p.setDescripcion(descripcion);
        return planes.save(p);
    }

    private Cliente crearCliente(String nombre, String apellido, String dni, String telefono, String email) {
        Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setDni(dni);
        c.setTelefono(telefono);
        c.setEmail(email);
        return clienteService.guardar(c);
    }

    private void crearClase(String nombre, String instructor, java.time.LocalDateTime fechaHora, int cupo) {
        Clase c = new Clase();
        c.setNombre(nombre);
        c.setInstructor(instructor);
        c.setFechaHora(fechaHora);
        c.setCupo(cupo);
        clases.save(c);
    }
}
