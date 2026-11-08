package com.forja.gimnasio.repository;

import com.forja.gimnasio.model.Clase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Long> {

    List<Clase> findByFechaHoraGreaterThanEqualOrderByFechaHora(LocalDateTime desde);
}
