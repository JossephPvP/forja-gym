package com.forja.gimnasio.repository;

import com.forja.gimnasio.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    boolean existsByClienteIdAndClaseIdAndCanceladaFalse(Long clienteId, Long claseId);

    long countByClaseIdAndCanceladaFalse(Long claseId);

    List<Reserva> findByClaseIdAndCanceladaFalseOrderByFechaReservaAsc(Long claseId);
}
