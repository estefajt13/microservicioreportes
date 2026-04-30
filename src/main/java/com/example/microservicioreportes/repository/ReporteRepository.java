package com.example.microservicioreportes.repository;

import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.model.EstadoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    // Reportes activos de un ciudadano específico
    List<Reporte> findByUidCiudadanoAndActivoTrue(String uidCiudadano);

    // Todos los reportes activos (para funcionario/admin - Sprint 2)
    List<Reporte> findByActivoTrue();

    // Reportes activos de un área específica (para funcionario)
    @Query("SELECT r FROM Reporte r WHERE r.activo = true AND r.tipoReporte.area.nombre = :nombreArea")
    List<Reporte> findByAreaNombre(@Param("nombreArea") String nombreArea);

    // Reportes activos de un área con filtro por estado
    @Query("SELECT r FROM Reporte r WHERE r.activo = true AND r.tipoReporte.area.nombre = :nombreArea AND r.estado = :estado")
    List<Reporte> findByAreaNombreAndEstado(@Param("nombreArea") String nombreArea, @Param("estado") EstadoReporte estado);

    // Reportes activos de un área con rango de fechas
    @Query("SELECT r FROM Reporte r WHERE r.activo = true AND r.tipoReporte.area.nombre = :nombreArea AND r.fechaReporte BETWEEN :fechaDesde AND :fechaHasta")
    List<Reporte> findByAreaNombreAndFechaBetween(@Param("nombreArea") String nombreArea, @Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);

    // Reportes asignados a un funcionario específico
    List<Reporte> findByUidFuncionarioAndActivoTrue(String uidFuncionario);

    // Métricas por área
    @Query("SELECT r.estado, COUNT(r) FROM Reporte r WHERE r.activo = true AND r.tipoReporte.area.nombre = :nombreArea GROUP BY r.estado")
    List<Object[]> countByEstadoByArea(@Param("nombreArea") String nombreArea);

    // Total de reportes por área
    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.activo = true AND r.tipoReporte.area.nombre = :nombreArea")
    Long countByAreaNombre(@Param("nombreArea") String nombreArea);
}
