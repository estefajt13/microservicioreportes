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

    // ==================== CONSULTAS PARA ADMIN DASHBOARD ====================

    // Total de reportes activos
    @Query("SELECT COUNT(r) FROM Reporte r WHERE r.activo = true")
    long countTotalReportes();

    // Conteo de reportes por estado
    @Query("SELECT r.estado, COUNT(r) FROM Reporte r WHERE r.activo = true GROUP BY r.estado")
    List<Object[]> countByEstado();

    // Reportes por área (nombre del área y total)
    @Query("SELECT r.tipoReporte.area.nombre, COUNT(r) FROM Reporte r WHERE r.activo = true GROUP BY r.tipoReporte.area.nombre ORDER BY COUNT(r) DESC")
    List<Object[]> countByArea();

    // Reportes por área filtrados por estado
    @Query("SELECT r.tipoReporte.area.nombre, COUNT(r) FROM Reporte r WHERE r.activo = true AND r.estado = :estado GROUP BY r.tipoReporte.area.nombre ORDER BY COUNT(r) DESC")
    List<Object[]> countByAreaByEstado(@Param("estado") EstadoReporte estado);

    // Reportes por zona con coordenadas (para mapa)
    @Query("SELECT r.zona, r.latitud, r.longitud, COUNT(r) FROM Reporte r WHERE r.activo = true AND r.zona IS NOT NULL AND r.latitud IS NOT NULL AND r.longitud IS NOT NULL GROUP BY r.zona, r.latitud, r.longitud ORDER BY COUNT(r) DESC")
    List<Object[]> getMapDataByZone();

    // Reportes por área con coordenadas (para mapa)
    @Query("SELECT r.tipoReporte.area.nombre, r.latitud, r.longitud, COUNT(r) FROM Reporte r WHERE r.activo = true AND r.latitud IS NOT NULL AND r.longitud IS NOT NULL GROUP BY r.tipoReporte.area.nombre, r.latitud, r.longitud ORDER BY COUNT(r) DESC")
    List<Object[]> getMapDataByArea();

    // Reportes creados por día en un rango de fechas
    @Query("SELECT DATE(r.fechaReporte), COUNT(r) FROM Reporte r WHERE r.activo = true AND r.fechaReporte BETWEEN :fechaDesde AND :fechaHasta GROUP BY DATE(r.fechaReporte) ORDER BY DATE(r.fechaReporte)")
    List<Object[]> countByDateBetween(@Param("fechaDesde") LocalDateTime fechaDesde, @Param("fechaHasta") LocalDateTime fechaHasta);

    // Reportes creados por día en la última semana
    @Query("SELECT DATE(r.fechaReporte), COUNT(r) FROM Reporte r WHERE r.activo = true AND r.fechaReporte >= :fechaDesde GROUP BY DATE(r.fechaReporte) ORDER BY DATE(r.fechaReporte)")
    List<Object[]> countByDateLastWeek(@Param("fechaDesde") LocalDateTime fechaDesde);

    // Reportes creados por día en el último mes
    @Query("SELECT DATE(r.fechaReporte), COUNT(r) FROM Reporte r WHERE r.activo = true AND r.fechaReporte >= :fechaDesde GROUP BY DATE(r.fechaReporte) ORDER BY DATE(r.fechaReporte)")
    List<Object[]> countByDateLastMonth(@Param("fechaDesde") LocalDateTime fechaDesde);

    // Reportes creados por mes en el último año
    @Query("SELECT DATE_FORMAT(r.fechaReporte, '%Y-%m'), COUNT(r) FROM Reporte r WHERE r.activo = true AND r.fechaReporte >= :fechaDesde GROUP BY DATE_FORMAT(r.fechaReporte, '%Y-%m') ORDER BY DATE_FORMAT(r.fechaReporte, '%Y-%m')")
    List<Object[]> countByMonthLastYear(@Param("fechaDesde") LocalDateTime fechaDesde);
}
