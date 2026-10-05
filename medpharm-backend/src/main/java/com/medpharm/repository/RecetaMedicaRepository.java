package com.medpharm.repository;

import com.medpharm.model.RecetaMedica;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaMedicaRepository extends JpaRepository<RecetaMedica, Long> {

    @Override
    @EntityGraph(attributePaths = {"medico", "detalles", "detalles.medicamento"})
    List<RecetaMedica> findAll();

    @EntityGraph(attributePaths = {"medico", "detalles", "detalles.medicamento"})
    List<RecetaMedica> findByEstado(String estado);

    boolean existsByCodigoReceta(String codigoReceta);
}