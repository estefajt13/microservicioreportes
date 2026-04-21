package com.example.microservicioreportes.service;

import com.example.microservicioreportes.model.TipoReporte;
import com.example.microservicioreportes.repository.TipoReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoReporteService {

    @Autowired
    private TipoReporteRepository repo;

    public List<TipoReporte> listar() {
        return repo.findAll();
    }

    public List<TipoReporte> listarPorArea(Long idArea) {
        return repo.findByAreaId(idArea);
    }

    public Optional<TipoReporte> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public TipoReporte crear(TipoReporte tipo) {
        return repo.save(tipo);
    }
}