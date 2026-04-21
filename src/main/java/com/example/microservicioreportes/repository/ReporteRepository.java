package com.example.microservicioreportes.repository;

import com.example.microservicioreportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // Reportes activos de un ciudadano específico
    List<Reporte> findByUidCiudadanoAndActivoTrue(String uidCiudadano);

    // Todos los reportes activos (para funcionario/admin - Sprint 2)
    List<Reporte> findByActivoTrue();
}