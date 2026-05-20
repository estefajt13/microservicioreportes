package com.example.microservicioreportes.controller;

import com.example.microservicioreportes.dto.ReporteDetalleDTO;
import com.example.microservicioreportes.model.HistorialCambio;
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
    public ResponseEntity<List<Reporte>> listar(
            @PathVariable String uid,
            @RequestParam(name = "limit", required = false) Integer limit) {
        return ResponseEntity.ok(service.listarPorCiudadano(uid, limit));
    }

    // Historial visible para ciudadano en un reporte específico
    @GetMapping("/ciudadano/{uid}/reports/{id}/history")
    public ResponseEntity<List<HistorialCambio>> historialCiudadano(
            @PathVariable String uid,
            @PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerHistorialVisibleParaCiudadano(uid, id));
    }

    // Ver detalle
    @GetMapping("/{id}")
    public ResponseEntity<ReporteDetalleDTO> detalle(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(this::mapToDetalleDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private ReporteDetalleDTO mapToDetalleDto(Reporte reporte) {
        ReporteDetalleDTO dto = new ReporteDetalleDTO();
        dto.setId(reporte.getId());
        dto.setUidCiudadano(reporte.getUidCiudadano());
        dto.setTipoReporteId(reporte.getTipoReporte() != null ? reporte.getTipoReporte().getId() : null);
        dto.setNombreTipoReporte(reporte.getTipoReporte() != null ? reporte.getTipoReporte().getNombre() : null);
        dto.setNombreArea(reporte.getTipoReporte() != null && reporte.getTipoReporte().getArea() != null ? reporte.getTipoReporte().getArea().getNombre() : null);
        dto.setAsunto(reporte.getAsunto());
        dto.setDescripcion(reporte.getDescripcion());
        dto.setEstado(reporte.getEstado() != null ? reporte.getEstado().name() : null);
        dto.setPrioridad(reporte.getPrioridad());
        dto.setLatitud(reporte.getLatitud());
        dto.setLongitud(reporte.getLongitud());
        dto.setDireccion(reporte.getDireccion());
        dto.setZona(reporte.getZona());
        dto.setUidFuncionario(reporte.getUidFuncionario());
        dto.setClusterId(reporte.getClusterId());
        dto.setActivo(reporte.getActivo());
        dto.setFechaReporte(reporte.getFechaReporte());
        return dto;
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