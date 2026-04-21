package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.model.TipoReporte;
import com.example.microservicioreportes.service.TipoReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tipos-reporte")
@CrossOrigin(origins = "*")
public class TipoReporteController {

    @Autowired
    private TipoReporteService service;

    @GetMapping
    public ResponseEntity<List<TipoReporte>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/area/{idArea}")
    public ResponseEntity<List<TipoReporte>> listarPorArea(@PathVariable Long idArea) {
        return ResponseEntity.ok(service.listarPorArea(idArea));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoReporte> detalle(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TipoReporte> crear(@RequestBody TipoReporte tipo) {
        return ResponseEntity.ok(service.crear(tipo));
    }
}