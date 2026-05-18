package com.example.microservicioreportes.service;

import com.example.microservicioreportes.model.ClusterReporte;
import com.example.microservicioreportes.model.EstadoReporte;
import com.example.microservicioreportes.model.HistorialCambio;
import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.repository.ClusterReporteRepository;
import com.example.microservicioreportes.repository.HistorialRepository;
import com.example.microservicioreportes.repository.ReporteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Operaciones masivas sobre un cluster: asignar, cambiar estado, cerrar.
 * Toda la logica vive en reportes porque actua sobre la tabla de reportes
 * (y los historiales correspondientes), evitando llamadas HTTP a analitica.
 */
@Service
public class ClusterReportesService {

    private static final Logger log = LoggerFactory.getLogger(ClusterReportesService.class);

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private ClusterReporteRepository clusterRepository;

    @Autowired
    private HistorialRepository historialRepository;

    @Transactional
    public ClusterReporte assignCluster(String uidFuncionario, String areaNombre, Long clusterId) {
        ClusterReporte cluster = obtenerClusterDelAreaDelFuncionario(clusterId, areaNombre);
        List<Reporte> reportes = reporteRepository.findByClusterIdAndActivoTrue(clusterId);

        cluster.setUidFuncionario(uidFuncionario);
        clusterRepository.save(cluster);

        int afectados = 0;
        for (Reporte r : reportes) {
            if (r.getEstado() == EstadoReporte.pendiente) {
                r.setUidFuncionario(uidFuncionario);
                r.setEstado(EstadoReporte.en_revision);
                reporteRepository.save(r);
                registrarHistorial(r, uidFuncionario, "asignacion",
                        "Asignado al funcionario " + uidFuncionario + " (via cluster " + clusterId + ")",
                        null, false);
                afectados++;
            }
        }
        log.info("Cluster {} asignado a {}: {} reporte(s) actualizados (de {})",
                clusterId, uidFuncionario, afectados, reportes.size());
        return cluster;
    }

    @Transactional
    public ClusterReporte updateClusterStatus(String uidFuncionario, String areaNombre, Long clusterId,
                                              String nuevoEstadoStr, String comentario, Boolean notificarCiudadano) {
        if (nuevoEstadoStr == null || nuevoEstadoStr.isBlank()) {
            throw new IllegalArgumentException("El estado es requerido");
        }
        EstadoReporte nuevoEstado;
        try {
            nuevoEstado = EstadoReporte.valueOf(nuevoEstadoStr.trim());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado invalido: " + nuevoEstadoStr);
        }

        ClusterReporte cluster = obtenerClusterDelAreaDelFuncionario(clusterId, areaNombre);
        List<Reporte> reportes = reporteRepository.findByClusterIdAndActivoTrue(clusterId);

        cluster.setEstadoCluster(nuevoEstado);
        clusterRepository.save(cluster);

        boolean visibleCiudadano = notificarCiudadano != null && notificarCiudadano;
        int afectados = 0;
        for (Reporte r : reportes) {
            if (r.getEstado() == nuevoEstado) continue; // ya estaba en ese estado
            if (r.getEstado() == EstadoReporte.resuelto) continue; // no se reabren resueltos
            EstadoReporte estadoAnterior = r.getEstado();
            r.setEstado(nuevoEstado);
            reporteRepository.save(r);
            registrarHistorial(r, uidFuncionario, "estado",
                    "Cambio estado de " + estadoAnterior.name() + " a " + nuevoEstado.name()
                            + " (via cluster " + clusterId + ")",
                    comentario, visibleCiudadano);
            afectados++;
        }
        log.info("Cluster {} pasado a estado {}: {} reporte(s) actualizados (de {})",
                clusterId, nuevoEstado, afectados, reportes.size());
        return cluster;
    }

    @Transactional
    public ClusterReporte closeCluster(String uidFuncionario, String areaNombre, Long clusterId,
                                       String comentario, Boolean notificarCiudadano) {
        String com = (comentario == null || comentario.isBlank()) ? "Cierre de cluster" : comentario;
        Boolean notif = (notificarCiudadano == null) ? Boolean.TRUE : notificarCiudadano;
        return updateClusterStatus(uidFuncionario, areaNombre, clusterId, EstadoReporte.resuelto.name(), com, notif);
    }

    private ClusterReporte obtenerClusterDelAreaDelFuncionario(Long clusterId, String areaNombre) {
        ClusterReporte cluster = clusterRepository.findById(clusterId)
                .orElseThrow(() -> new NoSuchElementException("Cluster no encontrado con ID: " + clusterId));
        if (cluster.getNombreArea() != null && !cluster.getNombreArea().equalsIgnoreCase(areaNombre)) {
            throw new IllegalStateException("El cluster " + clusterId + " no pertenece al area '" + areaNombre + "'");
        }
        return cluster;
    }

    private void registrarHistorial(Reporte reporte, String uidUsuario, String tipo,
                                    String descripcion, String comentario, boolean visibleCiudadano) {
        HistorialCambio h = new HistorialCambio();
        h.setReporte(reporte);
        h.setUidUsuario(uidUsuario);
        h.setTipo(tipo);
        h.setDescripcion(descripcion);
        h.setComentario(comentario);
        h.setVisibleCiudadano(visibleCiudadano);
        historialRepository.save(h);
    }
}
