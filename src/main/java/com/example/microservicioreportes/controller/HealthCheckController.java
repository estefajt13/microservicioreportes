package com.example.microservicioreportes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
@CrossOrigin(origins = "*")
public class HealthCheckController {

    /**
     * GET /health
     * Endpoint para verificar el estado del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "microservicioreportes");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /
     * Endpoint raíz con información del servicio
     */
    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Microservicio de Reportes - BugAReporta");
        response.put("version", "1.0.0");
        response.put("endpoints", Map.of(
            "health", "/health",
            "admin_summary", "/admin/reports/summary",
            "admin_daily_processes", "/admin/reports/daily-processes",
            "admin_by_area", "/admin/reports/by-area",
            "reportes", "/reportes"
        ));
        return ResponseEntity.ok(response);
    }
}