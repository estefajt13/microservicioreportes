package com.example.microservicioreportes.service;

import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.model.EstadoReporte;
import com.example.microservicioreportes.model.Area;
import com.example.microservicioreportes.repository.ReporteRepository;
import com.example.microservicioreportes.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class FuncionarioService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private AreaRepository areaRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Obtener reportes del área del funcionario con filtros opcionales
     */
    @Transactional(readOnly = true)
    public List<Reporte> getReportsByFuncionarioArea(String uidFuncionario, String areaNombre, 
                                                      String estado, String fechaDesde, String fechaHasta) {
        
        // Primero verificamos que el funcionario exista y tenga área asignada
        // (esto vendría de Firebase, por ahora usamos el área que nos pasan)
        if (areaNombre == null || areaNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El área del funcionario es requerida");
        }

        List<Reporte> reportes;

        // Aplicar filtros
        if (estado != null && !estado.trim().isEmpty()) {
            try {
                EstadoReporte estadoEnum = EstadoReporte.valueOf(estado.trim());
                if (fechaDesde != null && !fechaDesde.trim().isEmpty() && 
                    fechaHasta != null && !fechaHasta.trim().isEmpty()) {
                    LocalDateTime desde = LocalDateTime.parse(fechaDesde + "T00:00:00");
                    LocalDateTime hasta = LocalDateTime.parse(fechaHasta + "T23:59:59");
                    reportes = reporteRepository.findByAreaNombreAndFechaBetween(areaNombre, desde, hasta);
                    // Filtramos por estado manualmente
                    reportes.removeIf(r -> r.getEstado() != estadoEnum);
                } else {
                    reportes = reporteRepository.findByAreaNombreAndEstado(areaNombre, estadoEnum);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Estado inválido: " + estado);
            }
        } else if (fechaDesde != null && !fechaDesde.trim().isEmpty() && 
                   fechaHasta != null && !fechaHasta.trim().isEmpty()) {
            LocalDateTime desde = LocalDateTime.parse(fechaDesde + "T00:00:00");
            LocalDateTime hasta = LocalDateTime.parse(fechaHasta + "T23:59:59");
            reportes = reporteRepository.findByAreaNombreAndFechaBetween(areaNombre, desde, hasta);
        } else {
            reportes = reporteRepository.findByAreaNombre(areaNombre);
        }

        return reportes;
    }

    /**
     * Obtener un reporte específico por ID, verificando que pertenezca al área del funcionario
     */
    @Transactional(readOnly = true)
    public Reporte getReportByIdAndArea(String uidFuncionario, String areaNombre, Long reporteId) {
        Reporte reporte = reporteRepository.findById(reporteId)
            .orElseThrow(() -> new NoSuchElementException("Reporte no encontrado con ID: " + reporteId));
        
        // Verificar que el reporte pertenezca al área del funcionario
        if (!reporte.getTipoReporte().getArea().getNombre().equals(areaNombre)) {
            throw new IllegalStateException("No tienes permiso para ver this reporte - no pertenece a tu área");
        }
        
        return reporte;
    }

    /**
     * Asignarse un reporte (cambiar a en_revision y asignar uid del funcionario)
     */
    @Transactional
    public Reporte assignReport(String uidFuncionario, String areaNombre, Long reporteId) {
        Reporte reporte = getReportByIdAndArea(uidFuncionario, areaNombre, reporteId);
        
        // Solo se pueden asignar reportes pendientes
        if (reporte.getEstado() != EstadoReporte.pendiente) {
            throw new IllegalStateException("Solo se pueden asignar reportes en estado pendiente");
        }
        
        reporte.setUidFuncionario(uidFuncionario);
        reporte.setEstado(EstadoReporte.en_revision);
        
        return reporteRepository.save(reporte);
    }

    /**
     * Actualizar el estado de un reporte
     */
    @Transactional
    public Reporte updateReportStatus(String uidFuncionario, String areaNombre, Long reporteId, String nuevoEstado) {
        Reporte reporte = getReportByIdAndArea(uidFuncionario, areaNombre, reporteId);
        
        try {
            EstadoReporte estadoEnum = EstadoReporte.valueOf(nuevoEstado.trim());
            
            // Validar transiciones de estado
            validarTransicionEstado(reporte.getEstado(), estadoEnum);
            
            reporte.setEstado(estadoEnum);
            return reporteRepository.save(reporte);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }
    }

    /**
     * Validar que la transición de estado sea válida
     */
    private void validarTransicionEstado(EstadoReporte estadoActual, EstadoReporte estadoNuevo) {
        // Reglas de transición:
        // pendiente -> en_revision, en_proceso (si se asigna directamente)
        // en_revision -> en_proceso, pendiente (si se devuelve)
        // en_proceso -> resuelto, en_revision (si necesita más revisión)
        // resuelto -> (no puede cambiar)
        
        if (estadoActual == EstadoReporte.resuelto) {
            throw new IllegalStateException("No se puede cambiar el estado de un reporte resuelto");
        }
        
        if (estadoActual == EstadoReporte.pendiente && 
            estadoNuevo != EstadoReporte.en_revision && 
            estadoNuevo != EstadoReporte.en_proceso) {
            throw new IllegalStateException("Desde pendiente solo se puede pasar a en_revision o en_proceso");
        }
        
        if (estadoActual == EstadoReporte.en_revision && 
            estadoNuevo != EstadoReporte.en_proceso && 
            estadoNuevo != EstadoReporte.pendiente) {
            throw new IllegalStateException("Desde en_revision solo se puede pasar a en_proceso o pendiente");
        }
        
        if (estadoActual == EstadoReporte.en_proceso && 
            estadoNuevo != EstadoReporte.resuelto && 
            estadoNuevo != EstadoReporte.en_revision) {
            throw new IllegalStateException("Desde en_proceso solo se puede pasar a resuelto o en_revision");
        }
    }

    /**
     * Obtener métricas del dashboard para un área específica
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardMetrics(String uidFuncionario, String areaNombre) {
        Map<String, Object> metrics = new HashMap<>();
        
        // Total de reportes
        Long total = reporteRepository.countByAreaNombre(areaNombre);
        metrics.put("total", total);
        
        // Contar por estado
        List<Object[]> countsByEstado = reporteRepository.countByEstadoByArea(areaNombre);
        for (Object[] row : countsByEstado) {
            EstadoReporte estado = (EstadoReporte) row[0];
            Long count = (Long) row[1];
            metrics.put(estado.name(), count);
        }
        
        // Asegurar que todos los estados estén presentes (aunque sea 0)
        for (EstadoReporte estado : EstadoReporte.values()) {
            metrics.putIfAbsent(estado.name(), 0L);
        }
        
        // Reportes asignados al funcionario
        List<Reporte> asignados = reporteRepository.findByUidFuncionarioAndActivoTrue(uidFuncionario);
        metrics.put("asignados", (long) asignados.size());
        
        return metrics;
    }

    /**
     * Obtener todas las áreas disponibles
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllAreas() {
        List<Area> areas = areaRepository.findAll();
        List<Map<String, Object>> areaList = new ArrayList<>();
        
        for (Area area : areas) {
            Map<String, Object> areaMap = new HashMap<>();
            areaMap.put("id", area.getId());
            areaMap.put("nombre", area.getNombre());
            areaList.add(areaMap);
        }
        
        return areaList;
    }
}