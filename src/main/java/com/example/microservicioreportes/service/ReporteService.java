package com.example.microservicioreportes.service;

import com.example.microservicioreportes.dto.AreaReportDTO;
import com.example.microservicioreportes.dto.DailyProcessDTO;
import com.example.microservicioreportes.dto.ReportSummaryDTO;
import com.example.microservicioreportes.dto.ReportSummaryDTO.TiemposDTO;
import com.example.microservicioreportes.dto.ReportSummaryDTO.PorEstadoDTO;
import com.example.microservicioreportes.dto.ReportSummaryDTO.MapDataDTO;
import com.example.microservicioreportes.model.EstadoReporte;
import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.model.TipoReporte;
import com.example.microservicioreportes.repository.HistorialRepository;
import com.example.microservicioreportes.repository.ReporteRepository;
import com.example.microservicioreportes.repository.TipoReporteRepository;
import com.example.microservicioreportes.service.client.AnaliticaClient;
import com.example.microservicioreportes.service.client.AnaliticaNuevoReporteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

    @Autowired
    private ReporteRepository repo;

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private TipoReporteRepository tipoReporteRepo;

    @Autowired
    private AnaliticaClient analiticaClient;

    // Crear reporte
    public Reporte crear(Reporte reporte) {
        reporte.setActivo(true);
        reporte.setEstado(EstadoReporte.pendiente);
        reporte.setFechaReporte(LocalDateTime.now());
        Reporte guardado = repo.save(reporte);

        try {
            AnaliticaNuevoReporteDTO evento = construirEventoAnalitica(guardado);
            Long clusterId = analiticaClient.enviarNuevoReporte(evento);
            if (clusterId != null) {
                guardado.setClusterId(clusterId);
                guardado = repo.save(guardado);
                log.info("Notificado microservicio de analítica para reporte {} y asignado cluster {}", guardado.getId(), clusterId);
            } else {
                log.warn("Microservicio de analítica no devolvió clusterId para reporte {}", guardado.getId());
            }
        } catch (Exception e) {
            log.error("No se pudo notificar al microservicio de analítica para reporte {}: {}", guardado.getId(), e.getMessage(), e);
        }

        return guardado;
    }

    private AnaliticaNuevoReporteDTO construirEventoAnalitica(Reporte reporte) {
        // El reporte recién guardado puede tener tipoReporte con solo el id seteado
        // (porque la app envía {"tipoReporte":{"id":X}}). Recargamos desde la BD para
        // tener nombre y área disponibles.
        Long idTipo = reporte.getTipoReporte() != null ? reporte.getTipoReporte().getId() : null;
        if (idTipo == null) {
            throw new IllegalStateException("Reporte " + reporte.getId() + " sin id_tipo, no se puede notificar a analítica");
        }
        TipoReporte tipo = tipoReporteRepo.findById(idTipo)
            .orElseThrow(() -> new IllegalStateException("TipoReporte " + idTipo + " no existe"));

        AnaliticaNuevoReporteDTO dto = new AnaliticaNuevoReporteDTO();
        dto.setIdReporte(reporte.getId());
        dto.setIdTipoReporte(tipo.getId());
        dto.setNombreTipoReporte(tipo.getNombre());
        dto.setNombreArea(tipo.getArea() != null ? tipo.getArea().getNombre() : null);
        dto.setLatitud(reporte.getLatitud());
        dto.setLongitud(reporte.getLongitud());
        dto.setFechaReporte(reporte.getFechaReporte().toString());
        return dto;
    }

    // Mis reportes por ciudadano
    public List<Reporte> listarPorCiudadano(String uid) {
        return listarPorCiudadano(uid, null);
    }

    public List<Reporte> listarPorCiudadano(String uid, Integer limit) {
        if (limit != null && limit > 0) {
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "fechaReporte"));
            return repo.findByUidCiudadanoAndActivoTrue(uid, pageable);
        }
        return repo.findByUidCiudadanoAndActivoTrue(uid);
    }

    // Ver detalle
    public Optional<Reporte> obtenerPorId(Long id) {
        return repo.findById(id)
                .filter(r -> r.getActivo());
    }

    public List<HistorialCambio> obtenerHistorialVisibleParaCiudadano(String uidCiudadano, Long reporteId) {
        Reporte reporte = repo.findById(reporteId)
                .filter(r -> r.getActivo() && uidCiudadano != null && uidCiudadano.equals(r.getUidCiudadano()))
                .orElseThrow(() -> new IllegalStateException("Reporte no encontrado o no pertenece al ciudadano"));

        return historialRepository.findVisibleToCiudadano(reporte.getId());
    }

    // Editar reporte
    public Reporte editar(Long id, Reporte datos) {
        Reporte r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        if (datos.getAsunto() != null && !datos.getAsunto().isEmpty()) {
            r.setAsunto(datos.getAsunto());
        }
        if (datos.getDescripcion() != null && !datos.getDescripcion().isEmpty()) {
            r.setDescripcion(datos.getDescripcion());
        }
        if (datos.getDireccion() != null) {
            r.setDireccion(datos.getDireccion());
        }
        if (datos.getLatitud() != null) {
            r.setLatitud(datos.getLatitud());
        }
        if (datos.getLongitud() != null) {
            r.setLongitud(datos.getLongitud());
        }

        return repo.save(r);
    }

    // Eliminar lógico
    public void eliminar(Long id) {
        Reporte r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        r.setActivo(false);
        repo.save(r);
    }

    // ==================== MÉTODOS PARA ADMIN DASHBOARD ====================

    /**
     * Obtiene el resumen general de todas las métricas de reportes
     */
    public ReportSummaryDTO getReportSummary() {
        ReportSummaryDTO summary = new ReportSummaryDTO();

        // Total de reportes
        summary.setTotalReportes(repo.countTotalReportes());

        // Conteo por estado
        List<Object[]> estadoCounts = repo.countByEstado();
        TiemposDTO tiempos = new TiemposDTO();
        PorEstadoDTO porEstado = new PorEstadoDTO();

        for (Object[] row : estadoCounts) {
            EstadoReporte estado = (EstadoReporte) row[0];
            long count = ((Number) row[1]).longValue();

            switch (estado) {
                case pendiente:
                    tiempos.setPendientes(count);
                    porEstado.setPendiente(count);
                    break;
                case en_revision:
                    tiempos.setEnRevision(count);
                    porEstado.setEn_revision(count);
                    break;
                case en_proceso:
                    tiempos.setEnProceso(count);
                    porEstado.setEn_proceso(count);
                    break;
                case resuelto:
                    tiempos.setResueltos(count);
                    porEstado.setResuelto(count);
                    break;
            }
        }

        summary.setTiempos(tiempos);
        summary.setPorEstado(porEstado);

        // Average resolution hours (simulado - se podría calcular basado en el historial)
        summary.setAverageResolutionHours(calculateAverageResolutionHours());

        // Map data by zone
        summary.setMapDataByZone(getMapDataByZone());

        // Map data by area
        summary.setMapDataByArea(getMapDataByArea());

        return summary;
    }

    /**
     * Obtiene los procesos diarios según el período especificado
     */
    public List<DailyProcessDTO> getDailyProcesses(String periodo, LocalDate fechaDesde, LocalDate fechaHasta) {
        List<Object[]> results;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        if (periodo == null) {
            periodo = "semanal";
        }

        switch (periodo.toLowerCase()) {
            case "semanal":
                startDateTime = LocalDate.now().minusWeeks(1).atStartOfDay();
                endDateTime = LocalDateTime.now();
                results = repo.countByDateLastWeek(startDateTime);
                break;
            case "mensual":
                startDateTime = LocalDate.now().minusMonths(1).atStartOfDay();
                endDateTime = LocalDateTime.now();
                results = repo.countByDateLastMonth(startDateTime);
                break;
            case "anual":
                startDateTime = LocalDate.now().minusYears(1).atStartOfDay();
                endDateTime = LocalDateTime.now();
                results = repo.countByMonthLastYear(startDateTime);
                break;
            default:
                if (fechaDesde != null && fechaHasta != null) {
                    startDateTime = fechaDesde.atStartOfDay();
                    endDateTime = fechaHasta.atTime(23, 59, 59);
                    results = repo.countByDateBetween(startDateTime, endDateTime);
                } else {
                    // Por defecto, última semana
                    startDateTime = LocalDate.now().minusWeeks(1).atStartOfDay();
                    endDateTime = LocalDateTime.now();
                    results = repo.countByDateLastWeek(startDateTime);
                }
                break;
        }

        return convertToDailyProcessDTOs(results, periodo);
    }

    /**
     * Obtiene la distribución de reportes por área
     */
    public List<AreaReportDTO> getReportsByArea(EstadoReporte estado) {
        List<Object[]> results;
        long total = repo.countTotalReportes();

        if (estado != null) {
            results = repo.countByAreaByEstado(estado);
            // Recalcular total basado en el filtro
            total = results.stream().mapToLong(row -> ((Number) row[1]).longValue()).sum();
        } else {
            results = repo.countByArea();
        }

        List<AreaReportDTO> areaReports = new ArrayList<>();
        for (Object[] row : results) {
            String areaNombre = (String) row[0];
            long count = ((Number) row[1]).longValue();
            double porcentaje = total > 0 ? Math.round((count * 100.0 / total) * 100.0) / 100.0 : 0.0;
            areaReports.add(new AreaReportDTO(areaNombre, count, porcentaje));
        }

        return areaReports;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private List<MapDataDTO> getMapDataByZone() {
        List<Object[]> results = repo.getMapDataByZone();
        List<MapDataDTO> mapData = new ArrayList<>();

        for (Object[] row : results) {
            MapDataDTO dto = new MapDataDTO();
            dto.setId("zona_" + row[0]);
            dto.setLabel((String) row[0]);
            dto.setLat(row[1] != null ? (Double) row[1] : null);
            dto.setLng(row[2] != null ? (Double) row[2] : null);
            dto.setTotal(row[3] != null ? ((Number) row[3]).longValue() : 0);
            mapData.add(dto);
        }

        return mapData;
    }

    private List<MapDataDTO> getMapDataByArea() {
        List<Object[]> results = repo.getMapDataByArea();
        List<MapDataDTO> mapData = new ArrayList<>();

        for (Object[] row : results) {
            MapDataDTO dto = new MapDataDTO();
            dto.setId("area_" + row[0]);
            dto.setLabel((String) row[0]);
            dto.setLat(row[1] != null ? (Double) row[1] : null);
            dto.setLng(row[2] != null ? (Double) row[2] : null);
            dto.setTotal(row[3] != null ? ((Number) row[3]).longValue() : 0);
            mapData.add(dto);
        }

        return mapData;
    }

    private double calculateAverageResolutionHours() {
        // TODO: Implementar cálculo real basado en el historial de cambios
        // Por ahora, retorna un valor por defecto
        return 48.5;
    }

    private List<DailyProcessDTO> convertToDailyProcessDTOs(List<Object[]> results, String periodo) {
        List<DailyProcessDTO> dtos = new ArrayList<>();
        DateTimeFormatter formatter;

        if ("anual".equals(periodo.toLowerCase())) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        }

        for (Object[] row : results) {
            DailyProcessDTO dto = new DailyProcessDTO();
            String dateStr = row[0].toString();
            dto.setDate(dateStr);
            long count = row[1] != null ? ((Number) row[1]).longValue() : 0;
            dto.setTotal(count);
            dto.setCreados(count);
            dtos.add(dto);
        }

        return dtos;
    }
}
