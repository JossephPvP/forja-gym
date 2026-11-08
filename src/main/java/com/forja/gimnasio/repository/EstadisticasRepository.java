package com.forja.gimnasio.repository;

import com.forja.gimnasio.model.Asistencia;
import com.forja.gimnasio.model.Pago;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/** Consultas de apoyo para el panel de estadísticas. */
@Repository
public class EstadisticasRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Pago> pagosDesde(LocalDateTime desde) {
        return em.createQuery("select p from Pago p where p.fechaPago >= :desde", Pago.class)
                .setParameter("desde", desde)
                .getResultList();
    }

    /** Solo los ingresos que fueron permitidos (el cliente sí entró). */
    public List<Asistencia> ingresosDesde(LocalDateTime desde) {
        return em.createQuery(
                        "select a from Asistencia a where a.permitido = true and a.fechaHora >= :desde",
                        Asistencia.class)
                .setParameter("desde", desde)
                .getResultList();
    }
}
