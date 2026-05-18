package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.model.ClusterReporte;
import com.example.microservicioreportes.service.ClusterReportesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints para operaciones masivas sobre un cluster de reportes.
 * Antes vivian en analitica y hacian N llamadas HTTP de regreso a este
 * microservicio. Ahora se ejecutan directamente aqui sobre la BD compartida.
 */
@RestController
@RequestMapping("/funcionario/clusters")
@CrossOrigin(origins = "*")
public class FuncionarioClusterController {

    @Autowired
    private ClusterReportesService clusterReportesService;

    @PutMapping("/{clusterId}/assign")
    public ResponseEntity<ClusterReporte> assignCluster(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long clusterId) {
        return ResponseEntity.ok(clusterReportesService.assignCluster(uid, areaNombre, clusterId));
    }

    @PutMapping("/{clusterId}/status")
    public ResponseEntity<ClusterReporte> updateClusterStatus(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long clusterId,
            @RequestBody Map<String, Object> body) {
        String estado = (String) body.get("estado");
        String comentario = (String) body.get("comentario");
        Boolean notificar = (Boolean) body.get("notificarCiudadano");
        return ResponseEntity.ok(
                clusterReportesService.updateClusterStatus(uid, areaNombre, clusterId, estado, comentario, notificar));
    }

    @PutMapping("/{clusterId}/close")
    public ResponseEntity<ClusterReporte> closeCluster(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long clusterId,
            @RequestBody(required = false) Map<String, Object> body) {
        String comentario = body != null ? (String) body.get("comentario") : null;
        Boolean notificar = body != null ? (Boolean) body.get("notificarCiudadano") : null;
        return ResponseEntity.ok(
                clusterReportesService.closeCluster(uid, areaNombre, clusterId, comentario, notificar));
    }
}
