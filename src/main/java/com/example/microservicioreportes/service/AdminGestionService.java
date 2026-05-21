package com.example.microservicioreportes.service;

import com.example.microservicioreportes.dto.AdminMapPointDTO;
import com.example.microservicioreportes.model.EstadoReporte;
import com.example.microservicioreportes.model.ClusterReporte;
import com.example.microservicioreportes.model.HistorialCambio;
import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.repository.ClusterReporteRepository;
import com.example.microservicioreportes.repository.HistorialRepository;
import com.example.microservicioreportes.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminGestionService {

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private HistorialRepository historialRepository;

    @Autowired
    private ClusterReporteRepository clusterRepository;

    @Transactional(readOnly = true)
    public List<Reporte> getReports(String estado, String fechaDesde, String fechaHasta, String area) {
        EstadoReporte estadoEnum = null;
        if (estado != null && !estado.trim().isEmpty()) {
            try {
                estadoEnum = EstadoReporte.valueOf(estado.trim());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Estado inválido: " + estado);
            }
        }

        LocalDateTime desde = null;
        LocalDateTime hasta = null;
        if (fechaDesde != null && !fechaDesde.trim().isEmpty()) {
            desde = LocalDate.parse(fechaDesde.trim()).atStartOfDay();
        }
        if (fechaHasta != null && !fechaHasta.trim().isEmpty()) {
            hasta = LocalDate.parse(fechaHasta.trim()).atTime(23, 59, 59);
        }

        final EstadoReporte finalEstado = estadoEnum;
        final LocalDateTime finalDesde = desde;
        final LocalDateTime finalHasta = hasta;
        final String areaFilter = area == null ? "" : area.trim().toLowerCase();

        return reporteRepository.findByActivoTrue().stream()
                .filter(r -> finalEstado == null || r.getEstado() == finalEstado)
                .filter(r -> finalDesde == null || (r.getFechaReporte() != null && !r.getFechaReporte().isBefore(finalDesde)))
                .filter(r -> finalHasta == null || (r.getFechaReporte() != null && !r.getFechaReporte().isAfter(finalHasta)))
                .filter(r -> {
                    if (areaFilter.isEmpty()) return true;
                    String nombreArea = r.getTipoReporte() != null && r.getTipoReporte().getArea() != null
                            ? r.getTipoReporte().getArea().getNombre()
                            : "";
                    return nombreArea != null && nombreArea.toLowerCase().contains(areaFilter);
                })
                .sorted(Comparator.comparing(Reporte::getFechaReporte,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Reporte getReportById(Long id) {
        return reporteRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Reporte no encontrado con ID: " + id));
    }

    @Transactional
    public Reporte updateReport(Long id, String estado, String uidFuncionario,
                                String comentario, Boolean notificarCiudadano) {
        Reporte reporte = getReportById(id);
        EstadoReporte estadoAnterior = reporte.getEstado();
        boolean cambioEstado = false;

        if (uidFuncionario != null && !uidFuncionario.trim().isEmpty()) {
            reporte.setUidFuncionario(uidFuncionario.trim());
        }

        if (estado != null && !estado.trim().isEmpty()) {
            try {
                EstadoReporte nuevoEstado = EstadoReporte.valueOf(estado.trim());
                if (estadoAnterior != nuevoEstado) {
                    reporte.setEstado(nuevoEstado);
                    cambioEstado = true;
                }
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Estado inválido: " + estado);
            }
        }

        Reporte actualizado = reporteRepository.save(reporte);

        if (cambioEstado || (comentario != null && !comentario.trim().isEmpty())) {
            HistorialCambio h = new HistorialCambio();
            h.setReporte(actualizado);
            h.setUidUsuario("admin");
            h.setTipo(cambioEstado ? "estado" : (Boolean.TRUE.equals(notificarCiudadano) ? "mensaje_ciudadano" : "comentario_interno"));
            h.setDescripcion(cambioEstado
                    ? "Cambió estado de " + estadoAnterior.name() + " a " + actualizado.getEstado().name()
                    : (Boolean.TRUE.equals(notificarCiudadano) ? "Mensaje al ciudadano" : "Comentario interno"));
            h.setComentario(comentario);
            h.setVisibleCiudadano(Boolean.TRUE.equals(notificarCiudadano));
            historialRepository.save(h);
        }

        return actualizado;
    }

    @Transactional
    public HistorialCambio addComment(Long id, String comentario, Boolean visibleCiudadano) {
        if (comentario == null || comentario.trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario es obligatorio");
        }

        Reporte reporte = getReportById(id);
        HistorialCambio h = new HistorialCambio();
        h.setReporte(reporte);
        h.setUidUsuario("admin");
        h.setTipo(Boolean.TRUE.equals(visibleCiudadano) ? "mensaje_ciudadano" : "comentario_interno");
        h.setDescripcion(Boolean.TRUE.equals(visibleCiudadano) ? "Mensaje al ciudadano" : "Comentario interno");
        h.setComentario(comentario.trim());
        h.setVisibleCiudadano(Boolean.TRUE.equals(visibleCiudadano));
        return historialRepository.save(h);
    }

    @Transactional(readOnly = true)
    public List<HistorialCambio> getHistory(Long id) {
        getReportById(id);
        return historialRepository.findByReporteId(id);
    }

    @Transactional
    public Reporte reassign(Long id, String uidFuncionario) {
        if (uidFuncionario == null || uidFuncionario.trim().isEmpty()) {
            throw new IllegalArgumentException("uidFuncionario es obligatorio");
        }

        Reporte reporte = getReportById(id);
        reporte.setUidFuncionario(uidFuncionario.trim());
        Reporte actualizado = reporteRepository.save(reporte);

        HistorialCambio h = new HistorialCambio();
        h.setReporte(actualizado);
        h.setUidUsuario("admin");
        h.setTipo("asignacion");
        h.setDescripcion("Reasignado al funcionario " + uidFuncionario.trim());
        h.setComentario(null);
        h.setVisibleCiudadano(false);
        historialRepository.save(h);

        return actualizado;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getFuncionarios() {
        return reporteRepository.findByActivoTrue().stream()
                .map(Reporte::getUidFuncionario)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(uid -> !uid.isEmpty())
                .distinct()
                .map(uid -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("uid", uid);
                    item.put("nombre", uid);
                    item.put("email", null);
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminMapPointDTO> getMapPoints(String estado, String area) {
        EstadoReporte estadoEnum = null;
        if (estado != null && !estado.trim().isEmpty() && !"todos".equalsIgnoreCase(estado.trim())) {
            try {
                estadoEnum = EstadoReporte.valueOf(estado.trim());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Estado inválido: " + estado);
            }
        }

        String areaFilter = area == null ? "" : area.trim().toLowerCase();
    final EstadoReporte finalEstado = estadoEnum;

        List<Reporte> reportes = reporteRepository.findByActivoTrue().stream()
        .filter(r -> finalEstado == null || r.getEstado() == finalEstado)
                .filter(r -> {
                    if (areaFilter.isEmpty() || "todas".equals(areaFilter)) {
                        return true;
                    }
                    String nombreArea = r.getTipoReporte() != null && r.getTipoReporte().getArea() != null
                            ? r.getTipoReporte().getArea().getNombre()
                            : "";
                    String areaNombre = nombreArea == null ? "" : nombreArea.toLowerCase();
                    return areaNombre.contains(areaFilter);
                })
                .filter(r -> r.getLatitud() != null && r.getLongitud() != null)
                .collect(Collectors.toList());

        Map<Long, List<Reporte>> clusters = new LinkedHashMap<>();
        List<Reporte> individuales = new ArrayList<>();

        for (Reporte reporte : reportes) {
            if (reporte.getClusterId() == null) {
                individuales.add(reporte);
                continue;
            }

            clusters.computeIfAbsent(reporte.getClusterId(), key -> new ArrayList<>()).add(reporte);
        }

        List<AdminMapPointDTO> points = new ArrayList<>();

        for (Map.Entry<Long, List<Reporte>> entry : clusters.entrySet()) {
            Long clusterId = entry.getKey();
            List<Reporte> clusterReports = entry.getValue();
            if (clusterReports.isEmpty()) {
                continue;
            }

            ClusterReporte cluster = clusterRepository.findById(clusterId).orElse(null);
            double[] centro = computeClusterCenter(clusterReports, cluster);

            AdminMapPointDTO point = new AdminMapPointDTO();
            point.setId("cluster-" + clusterId);
            point.setKind("cluster");
            point.setClusterId(clusterId);
            point.setLabel(cluster != null && cluster.getNombreArea() != null
                    ? cluster.getNombreArea()
                    : "Cluster " + clusterId);
            point.setLat(centro[0]);
            point.setLng(centro[1]);
            point.setTotal(clusterReports.size());
            points.add(point);
        }

        for (Reporte reporte : individuales) {
            AdminMapPointDTO point = new AdminMapPointDTO();
            point.setId("report-" + reporte.getId());
            point.setKind("report");
            point.setClusterId(null);
            point.setLabel(reporte.getAsunto() != null && !reporte.getAsunto().isBlank()
                    ? reporte.getAsunto()
                    : (reporte.getTipoReporte() != null ? reporte.getTipoReporte().getNombre() : "Reporte " + reporte.getId()));
            point.setLat(reporte.getLatitud());
            point.setLng(reporte.getLongitud());
            point.setTotal(1);
            points.add(point);
        }

        return points;
    }

    private double[] computeClusterCenter(List<Reporte> clusterReports, ClusterReporte cluster) {
        if (cluster != null && cluster.getLatitudCentroide() != null && cluster.getLongitudCentroide() != null) {
            return new double[]{cluster.getLatitudCentroide(), cluster.getLongitudCentroide()};
        }

        double latSum = 0.0;
        double lngSum = 0.0;
        int count = 0;
        for (Reporte reporte : clusterReports) {
            if (reporte.getLatitud() == null || reporte.getLongitud() == null) {
                continue;
            }
            latSum += reporte.getLatitud();
            lngSum += reporte.getLongitud();
            count++;
        }

        if (count == 0) {
            return new double[]{0.0, 0.0};
        }

        return new double[]{latSum / count, lngSum / count};
    }
}
