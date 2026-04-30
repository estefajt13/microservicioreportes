package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.dto.AreaReportDTO;
import com.example.microservicioreportes.dto.DailyProcessDTO;
import com.example.microservicioreportes.dto.ReportSummaryDTO;
import com.example.microservicioreportes.model.EstadoReporte;
import com.example.microservicioreportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/reports")
@CrossOrigin(origins = "*")
public class AdminReportController {

    @Autowired
    private ReporteService service;

    /**
     * GET /admin/reports/summary
     * Devuelve un resumen general de todas las métricas de reportes.
     */
    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDTO> getReportSummary() {
        try {
            ReportSummaryDTO summary = service.getReportSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    /**
     * GET /admin/reports/daily-processes
     * Devuelve el número de reportes creados por día en un período determinado.
     * 
     * Parámetros de query opcionales:
     * - periodo: semanal, mensual, anual (default: semanal)
     * - fechaDesde: Fecha de inicio (formato ISO: YYYY-MM-DD)
     * - fechaHasta: Fecha de fin (formato ISO: YYYY-MM-DD)
     */
    @GetMapping("/daily-processes")
    public ResponseEntity<?> getDailyProcesses(
            @RequestParam(required = false) String periodo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta) {
        try {
            // Validar parámetros
            if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "fechaDesde no puede ser posterior a fechaHasta");
                error.put("code", "INVALID_DATE_RANGE");
                return ResponseEntity.badRequest().body(error);
            }

            List<DailyProcessDTO> processes = service.getDailyProcesses(periodo, fechaDesde, fechaHasta);
            
            // Si no hay datos, devolver 204 No Content
            if (processes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(processes);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("code", "INVALID_PARAMETER");
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            error.put("code", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /admin/reports/by-area
     * Devuelve la distribución de reportes por área.
     * 
     * Parámetros de query opcionales:
     * - estado: Filtrar por estado (pendiente, en_proceso, resuelto, etc.)
     */
    @GetMapping("/by-area")
    public ResponseEntity<?> getReportsByArea(
            @RequestParam(required = false) String estado) {
        try {
            EstadoReporte estadoEnum = null;
            
            // Convertir el string a enum si se proporciona
            if (estado != null && !estado.isEmpty()) {
                try {
                    estadoEnum = EstadoReporte.valueOf(estado);
                } catch (IllegalArgumentException e) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Estado inválido. Valores permitidos: pendiente, en_revision, en_proceso, resuelto");
                    error.put("code", "INVALID_STATUS");
                    return ResponseEntity.badRequest().body(error);
                }
            }

            List<AreaReportDTO> reports = service.getReportsByArea(estadoEnum);
            
            // Si no hay datos, devolver 204 No Content
            if (reports.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error interno del servidor");
            error.put("code", "INTERNAL_SERVER_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}