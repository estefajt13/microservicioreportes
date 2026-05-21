package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/interno/reportes")
@CrossOrigin(origins = "*")
public class InternalReportController {

    @Autowired
    private ReporteService reporteService;

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 1 — Procesos diarios por rango de fechas
    // GET /interno/reportes/diarios?fechaDesde=YYYY-MM-DD&fechaHasta=YYYY-MM-DD
    // Reutiliza countByDateBetween del repository
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/diarios")
    public ResponseEntity<?> getReportesDiarios(
            @RequestParam String fechaDesde,
            @RequestParam String fechaHasta) {
        try {
            List<Map<String, Object>> result = reporteService.getReportesDiarios(fechaDesde, fechaHasta);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener reportes diarios", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 2 — Reportes por área (con o sin detalle por estado)
    // GET /interno/reportes/por-area?conDetalle=false
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/por-area")
    public ResponseEntity<?> getReportesPorArea(
            @RequestParam(defaultValue = "false") boolean conDetalle) {
        try {
            List<Map<String, Object>> result = reporteService.getReportesPorArea(conDetalle);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener reportes por área", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 3 — Resueltos por período (semanal / mensual / anual)
    // GET /interno/reportes/resueltos-por-periodo?periodo=semanal
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/resueltos-por-periodo")
    public ResponseEntity<?> getResueltosporPeriodo(
            @RequestParam(defaultValue = "semanal") String periodo) {
        try {
            Map<String, Object> result = reporteService.getResueltosporPeriodo(periodo);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener resueltos por período", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 4 — Área con más incidencias activas (estado != resuelto)
    // GET /interno/reportes/area-mas-activa
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/area-mas-activa")
    public ResponseEntity<?> getAreaMasActiva() {
        try {
            Map<String, Object> result = reporteService.getAreaMasActiva();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener área más activa", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 5 — Tipos más frecuentes de la última semana
    // GET /interno/reportes/tipos-frecuentes?limite=5
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/tipos-frecuentes")
    public ResponseEntity<?> getTiposFrecuentes(
            @RequestParam(defaultValue = "5") int limite) {
        try {
            List<Map<String, Object>> result = reporteService.getTiposFrecuentes(limite);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener tipos frecuentes", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 6 — Tipos por área específica
    // GET /interno/reportes/tipos-por-area?area=Infraestructura vial y espacio público
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/tipos-por-area")
    public ResponseEntity<?> getTiposPorArea(
            @RequestParam String area) {
        try {
            List<Map<String, Object>> result = reporteService.getTiposPorArea(area);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener tipos por área", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 7 — Tendencia mensual (últimos 6 meses)
    // GET /interno/reportes/tendencia-mensual
    // Reutiliza countByMonthLastYear con fechaDesde = hace 6 meses
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/tendencia-mensual")
    public ResponseEntity<?> getTendenciaMensual() {
        try {
            List<Map<String, Object>> result = reporteService.getTendenciaMensual();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener tendencia mensual", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 8 — Reportes abandonados (sin resolver hace X días)
    // GET /interno/reportes/abandonados?dias=7
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/abandonados")
    public ResponseEntity<?> getReportesAbandonados(
            @RequestParam(defaultValue = "7") int dias) {
        try {
            Map<String, Object> result = reporteService.getReportesAbandonados(dias);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener reportes abandonados", "detalle", e.getMessage()));
        }
    }

    // ─────────────────────────────────────────────────────────────
    // ENDPOINT 9 — Tipo de reporte más frecuente global
    // GET /interno/reportes/tipo-mas-frecuente
    // ─────────────────────────────────────────────────────────────
    @GetMapping("/tipo-mas-frecuente")
    public ResponseEntity<?> getTipoMasFrecuente() {
        try {
            Map<String, Object> result = reporteService.getTipoMasFrecuente();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Error al obtener tipo más frecuente", "detalle", e.getMessage()));
        }
    }
}