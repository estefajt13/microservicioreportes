package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private ReporteService service;

    // Crear reporte
    @PostMapping
    public ResponseEntity<Reporte> crear(@RequestBody Reporte reporte) {
        return ResponseEntity.ok(service.crear(reporte));
    }

    // Mis reportes por ciudadano
    @GetMapping("/ciudadano/{uid}")
    public ResponseEntity<List<Reporte>> listar(@PathVariable String uid) {
        return ResponseEntity.ok(service.listarPorCiudadano(uid));
    }

    // Ver detalle
    @GetMapping("/{id}")
    public ResponseEntity<Reporte> detalle(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Editar reporte
    @PutMapping("/{id}")
    public ResponseEntity<Reporte> editar(
            @PathVariable Long id,
            @RequestBody Reporte datos) {
        return ResponseEntity.ok(service.editar(id, datos));
    }

    // Eliminar lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}