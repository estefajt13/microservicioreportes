package com.example.microservicioreportes.model;

import jakarta.persistence.*;

/**
 * Mapeo minimo de la tabla clusters_reporte (propietaria del microservicio
 * de analitica) para que reportes pueda actualizar estado_cluster y
 * uid_funcionario directamente en la BD compartida al cerrar/asignar/cambiar
 * estado de un cluster, sin tener que llamar via HTTP a analitica.
 */
@Entity
@Table(name = "clusters_reporte")
public class ClusterReporte {

    @Id
    private Long id;

    @Column(name = "nombre_area")
    private String nombreArea;

    @Column(name = "uid_funcionario")
    private String uidFuncionario;

    @Column(name = "latitud_centroide")
    private Double latitudCentroide;

    @Column(name = "longitud_centroide")
    private Double longitudCentroide;

    @Column(name = "total_reportes")
    private Integer totalReportes;

    @Column(name = "ids_reportes")
    private String idsReportes;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cluster")
    private EstadoReporte estadoCluster;

    @Column(name = "activo")
    private Boolean activo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }

    public String getUidFuncionario() { return uidFuncionario; }
    public void setUidFuncionario(String uidFuncionario) { this.uidFuncionario = uidFuncionario; }

    public Double getLatitudCentroide() { return latitudCentroide; }
    public void setLatitudCentroide(Double latitudCentroide) { this.latitudCentroide = latitudCentroide; }

    public Double getLongitudCentroide() { return longitudCentroide; }
    public void setLongitudCentroide(Double longitudCentroide) { this.longitudCentroide = longitudCentroide; }

    public Integer getTotalReportes() { return totalReportes; }
    public void setTotalReportes(Integer totalReportes) { this.totalReportes = totalReportes; }

    public String getIdsReportes() { return idsReportes; }
    public void setIdsReportes(String idsReportes) { this.idsReportes = idsReportes; }

    public EstadoReporte getEstadoCluster() { return estadoCluster; }
    public void setEstadoCluster(EstadoReporte estadoCluster) { this.estadoCluster = estadoCluster; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
