package com.example.microservicioreportes.repository;

import com.example.microservicioreportes.model.ClusterReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClusterReporteRepository extends JpaRepository<ClusterReporte, Long> {
}
