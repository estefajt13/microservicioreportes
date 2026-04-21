package com.example.microservicioreportes.service;

import com.example.microservicioreportes.model.Reporte;
import com.example.microservicioreportes.model.EstadoReporte;
import com.example.microservicioreportes.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReporteService {

    @Autowired
    private ReporteRepository repo;

    // Crear reporte
    public Reporte crear(Reporte reporte) {
        reporte.setActivo(true);
        reporte.setEstado(EstadoReporte.pendiente);
        reporte.setFechaReporte(LocalDateTime.now());
        return repo.save(reporte);
    }

    // Mis reportes por ciudadano
    public List<Reporte> listarPorCiudadano(String uid) {
        return repo.findByUidCiudadanoAndActivoTrue(uid);
    }

    // Ver detalle
    public Optional<Reporte> obtenerPorId(Long id) {
        return repo.findById(id)
                .filter(r -> r.getActivo());
    }

    // Editar reporte
    public Reporte editar(Long id, Reporte datos) {
    Reporte r = repo.findById(id)
            .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

    if (datos.getAsunto() != null && !datos.getAsunto().isEmpty()) {
        r.setAsunto(datos.getAsunto());
    }
    if (datos.getDescripcion() != null && !datos.getDescripcion().isEmpty()) {
        r.setDescripcion(datos.getDescripcion());
    }
    if (datos.getDireccion() != null) {
        r.setDireccion(datos.getDireccion());
    }
    if (datos.getLatitud() != null) {
        r.setLatitud(datos.getLatitud());
    }
    if (datos.getLongitud() != null) {
        r.setLongitud(datos.getLongitud());
    }

    return repo.save(r);
}

    // Eliminar lógico
    public void eliminar(Long id) {
        Reporte r = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
        r.setActivo(false);
        repo.save(r);
    }
}