package com.forja.gimnasio.repository;

import com.forja.gimnasio.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDni(String dni);

    List<Cliente> findByActivoTrueOrderByApellidoAscNombreAsc();

    long countByActivoTrue();

    @Query("""
            select c from Cliente c
            where c.activo = true and (
                lower(c.nombre) like lower(concat('%', :q, '%'))
                or lower(c.apellido) like lower(concat('%', :q, '%'))
                or c.dni like concat('%', :q, '%'))
            order by c.apellido, c.nombre
            """)
    List<Cliente> buscar(@Param("q") String q);
}
