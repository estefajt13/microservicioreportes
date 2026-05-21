package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.model.Area;
import com.example.microservicioreportes.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/areas")
public class AreaController {

    @Autowired
    private AreaService service;

    @GetMapping
    public ResponseEntity<List<Area>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Area> detalle(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Area> crear(@RequestBody Area area) {
        return ResponseEntity.ok(service.crear(area));
    }
}