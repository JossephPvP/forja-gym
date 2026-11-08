package com.forja.gimnasio.service;

import com.forja.gimnasio.exception.ReglaNegocioException;
import com.forja.gimnasio.model.Plan;
import com.forja.gimnasio.repository.PlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planes;

    public PlanService(PlanRepository planes) {
        this.planes = planes;
    }

    public List<Plan> listar() { return planes.findAllByOrderByPrecioAsc(); }

    public List<Plan> listarActivos() { return planes.findByActivoTrueOrderByPrecioAsc(); }

    public Plan obtener(Long id) {
        return planes.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("No se encontró el plan."));
    }

    public Plan guardar(Plan plan) { return planes.save(plan); }

    @Transactional
    public void cambiarEstado(Long id) {
        Plan p = obtener(id);
        p.setActivo(!p.isActivo());
        planes.save(p);
    }
}
