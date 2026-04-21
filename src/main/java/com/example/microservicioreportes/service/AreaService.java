package com.example.microservicioreportes.service;

import com.example.microservicioreportes.model.Area;
import com.example.microservicioreportes.repository.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AreaService {

    @Autowired
    private AreaRepository repo;

    public List<Area> listar() {
        return repo.findAll();
    }

    public Optional<Area> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public Area crear(Area area) {
        return repo.save(area);
    }
}