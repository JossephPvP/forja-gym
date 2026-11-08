package com.forja.gimnasio.repository;

import com.forja.gimnasio.model.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MembresiaRepository extends JpaRepository<Membresia, Long> {

    List<Membresia> findAllByOrderByFechaFinDesc();

    List<Membresia> findByClienteIdOrderByFechaFinDesc(Long clienteId);

    Optional<Membresia> findFirstByClienteIdAndCanceladaFalseOrderByFechaFinDesc(Long clienteId);

    @Query("""
            select count(m) from Membresia m
            where m.cancelada = false and m.fechaInicio <= :hoy and m.fechaFin >= :hoy
            """)
    long contarVigentes(@Param("hoy") LocalDate hoy);

    /** Membresías del cliente que cubren la fecha indicada. */
    @Query("""
            select m from Membresia m
            where m.cliente.id = :clienteId and m.cancelada = false
              and m.fechaInicio <= :fecha and m.fechaFin >= :fecha
            order by m.fechaFin desc
            """)
    List<Membresia> vigentesDe(@Param("clienteId") Long clienteId, @Param("fecha") LocalDate fecha);

    /** Vencen entre hoy y el límite y el cliente todavía no renovó. */
    @Query("""
            select m from Membresia m
            where m.cancelada = false and m.fechaInicio <= :hoy
              and m.fechaFin between :hoy and :limite
              and not exists (
                  select 1 from Membresia r
                  where r.cliente = m.cliente and r.cancelada = false and r.fechaFin > m.fechaFin)
            order by m.fechaFin
            """)
    List<Membresia> porVencer(@Param("hoy") LocalDate hoy, @Param("limite") LocalDate limite);

    /** Cuántas membresías del cliente se cruzan con el rango de fechas. */
    @Query("""
            select count(m) from Membresia m
            where m.cliente.id = :clienteId and m.cancelada = false
              and m.fechaInicio <= :fin and m.fechaFin >= :inicio
            """)
    long contarCruces(@Param("clienteId") Long clienteId,
                      @Param("inicio") LocalDate inicio,
                      @Param("fin") LocalDate fin);
}
