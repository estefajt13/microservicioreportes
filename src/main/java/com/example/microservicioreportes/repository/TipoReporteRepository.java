package com.example.microservicioreportes.repository;

import com.example.microservicioreportes.model.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoReporteRepository extends JpaRepository<TipoReporte, Long> {
    List<TipoReporte> findByAreaId(Long idArea);
}