package com.forja.gimnasio.repository;

import com.forja.gimnasio.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findAllByOrderByFechaPagoDesc();

    List<Pago> findByMembresiaClienteIdOrderByFechaPagoDesc(Long clienteId);

    /** Devuelve null si no hubo pagos en el rango. */
    @Query("select sum(p.monto) from Pago p where p.fechaPago >= :desde and p.fechaPago < :hasta")
    BigDecimal totalEntre(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);
}
