package com.forja.gimnasio.repository;

import com.forja.gimnasio.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findAllByOrderByPrecioAsc();

    List<Plan> findByActivoTrueOrderByPrecioAsc();
}
