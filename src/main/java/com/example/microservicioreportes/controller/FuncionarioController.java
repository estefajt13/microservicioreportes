package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.model.HistorialCambio;
import com.example.microservicioreportes.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    /**
     * Obtener el área del funcionario desde el header X-User-Area
     * (Enviado por el Next.js web panel después de verificar Firebase Auth)
     */
    private String getFuncionarioArea(@RequestHeader("X-User-Area") String area) {
        if (area == null || area.trim().isEmpty()) {
            throw new IllegalArgumentException("El funcionario no tiene un área asignada en su perfil");
        }
        return area;
    }

    /**
     * Obtener el UID del funcionario desde el header X-User-UID
     */
    private String getFuncionarioUid(@RequestHeader("X-User-UID") String uid) {
        if (uid == null || uid.trim().isEmpty()) {
            throw new IllegalArgumentException("UID de usuario es requerido");
        }
        return uid;
    }

    /**
     * GET /api/funcionario/reports
     * Obtener todos los reportes del área del funcionario con filtros opcionales
     * Headers: X-User-UID, X-User-Area
     * Query params: estado, fechaDesde (YYYY-MM-DD), fechaHasta (YYYY-MM-DD)
     */
    @GetMapping("/reports")
    public ResponseEntity<List<Reporte>> getReportsByArea(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta) {
        
        List<Reporte> reportes = funcionarioService.getReportsByFuncionarioArea(
            uid, areaNombre, estado, fechaDesde, fechaHasta);
        
        return ResponseEntity.ok(reportes);
    }

    /**
     * GET /api/funcionario/reports/{id}
     * Obtener detalle de un reporte específico
     */
    @GetMapping("/reports/{id}")
    public ResponseEntity<Reporte> getReportById(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long id) {
        
        Reporte reporte = funcionarioService.getReportByIdAndArea(uid, areaNombre, id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * PUT /api/funcionario/reports/{id}/assign
     * Asignarse un reporte (cambia estado a en_revision)
     */
    @PutMapping("/reports/{id}/assign")
    public ResponseEntity<Reporte> assignReport(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long id) {
        
        Reporte reporte = funcionarioService.assignReport(uid, areaNombre, id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * PUT /api/funcionario/reports/{id}/status
     * Actualizar estado de un reporte
     * Body: { "estado": "en_proceso", "comentario": "...", "notificarCiudadano": true }
     */
    @PutMapping("/reports/{id}/status")
    public ResponseEntity<Reporte> updateReportStatus(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        
        String nuevoEstado = (String) body.get("estado");
        String comentario = (String) body.get("comentario");
        Boolean notificarCiudadano = (Boolean) body.get("notificarCiudadano");
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Reporte reporte = funcionarioService.updateReportStatus(uid, areaNombre, id, nuevoEstado, comentario, notificarCiudadano);
        return ResponseEntity.ok(reporte);
    }

    /**
     * POST /api/funcionario/reports/{id}/comment
     * Agregar un comentario al reporte
     * Body: { "comentario": "...", "visibleCiudadano": true }
     */
    @PostMapping("/reports/{id}/comment")
    public ResponseEntity<HistorialCambio> addComment(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        
        String comentario = (String) body.get("comentario");
        Boolean visibleCiudadano = (Boolean) body.get("visibleCiudadano");
        
        if (comentario == null || comentario.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        HistorialCambio historial = funcionarioService.addComment(uid, areaNombre, id, comentario, visibleCiudadano);
        return ResponseEntity.ok(historial);
    }

    /**
     * GET /api/funcionario/reports/{id}/history
     * Obtener historial de un reporte
     */
    @GetMapping("/reports/{id}/history")
    public ResponseEntity<List<HistorialCambio>> getReportHistory(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre,
            @PathVariable Long id) {
        
        List<HistorialCambio> history = funcionarioService.getReportHistory(uid, areaNombre, id);
        return ResponseEntity.ok(history);
    }

    /**
     * GET /api/funcionario/dashboard/metrics
     * Obtener métricas del dashboard para el área del funcionario
     */
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics(
            @RequestHeader("X-User-UID") String uid,
            @RequestHeader("X-User-Area") String areaNombre) {
        
        Map<String, Object> metrics = funcionarioService.getDashboardMetrics(uid, areaNombre);
        return ResponseEntity.ok(metrics);
    }

    /**
     * GET /api/funcionario/areas
     * Obtener lista de todas las áreas disponibles
     */
    @GetMapping("/areas")
    public ResponseEntity<List<Map<String, Object>>> getAreas() {
        List<Map<String, Object>> areas = funcionarioService.getAllAreas();
        return ResponseEntity.ok(areas);
    }

    // Manejo de excepciones
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
    }
}
