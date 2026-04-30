package com.example.microservicioreportes.repository;

import com.example.microservicioreportes.model.HistorialCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialRepository extends JpaRepository<HistorialCambio, Long> {

    // Historial de un reporte ordenado por fecha (más reciente primero)
    @Query("SELECT h FROM HistorialCambio h WHERE h.reporte.id = :idReporte ORDER BY h.fechaCambio DESC")
    List<HistorialCambio> findByReporteId(@Param("idReporte") Long idReporte);

    // Solo los comentarios visibles para el ciudadano
    @Query("SELECT h FROM HistorialCambio h WHERE h.reporte.id = :idReporte AND h.visibleCiudadano = true ORDER BY h.fechaCambio DESC")
    List<HistorialCambio> findVisibleToCiudadano(@Param("idReporte") Long idReporte);

    // Contar cambios de estado
    @Query("SELECT COUNT(h) FROM HistorialCambio h WHERE h.reporte.id = :idReporte AND h.tipo = 'estado'")
    Long countEstadoChanges(@Param("idReporte") Long idReporte);
}