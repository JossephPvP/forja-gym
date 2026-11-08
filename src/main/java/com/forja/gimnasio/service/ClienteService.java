package com.forja.gimnasio.service;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Cliente;
import com.forja.gimnasio.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ClienteService {

    private final ClienteRepository clientes;

    public ClienteService(ClienteRepository clientes) {
        this.clientes = clientes;
    }

    public List<Cliente> listar(String q) {
        if (q == null || q.isBlank()) {
            return clientes.findByActivoTrueOrderByApellidoAscNombreAsc();
        }
        return clientes.buscar(q.trim());
    }

    public List<Cliente> listarActivos() {
        return clientes.findByActivoTrueOrderByApellidoAscNombreAsc();
    }

    public Cliente obtener(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el cliente."));
    }

    public long contarActivos() {
        return clientes.countByActivoTrue();
    }

    @Transactional
    public Cliente guardar(Cliente datos) {
        clientes.findByDni(datos.getDni()).ifPresent(otro -> {
            if (!Objects.equals(otro.getId(), datos.getId())) {
                throw new ReglaNegocioException("Ya existe un cliente con ese DNI.");
            }
        });

        if (datos.getId() == null) {
            return clientes.save(datos);
        }

        Cliente actual = obtener(datos.getId());
        actual.setNombre(datos.getNombre());
        actual.setApellido(datos.getApellido());
        actual.setDni(datos.getDni());
        actual.setTelefono(datos.getTelefono());
        actual.setEmail(datos.getEmail());
        return clientes.save(actual);
    }

    @Transactional
    public void darDeBaja(Long id) {
        Cliente c = obtener(id);
        c.setActivo(false);
        clientes.save(c);
    }
}
