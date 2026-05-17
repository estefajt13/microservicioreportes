package com.example.microservicioreportes.dto;

import java.time.LocalDateTime;

public class ReporteDetalleDTO {

    private Long id;
    private String uidCiudadano;
    private Long tipoReporteId;
    private String nombreTipoReporte;
    private String nombreArea;
    private String asunto;
    private String descripcion;
    private String estado;
    private String prioridad;
    private Double latitud;
    private Double longitud;
    private String direccion;
    private String zona;
    private String uidFuncionario;
    private Long clusterId;
    private Boolean activo;
    private LocalDateTime fechaReporte;

    public ReporteDetalleDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUidCiudadano() { return uidCiudadano; }
    public void setUidCiudadano(String uidCiudadano) { this.uidCiudadano = uidCiudadano; }
    public Long getTipoReporteId() { return tipoReporteId; }
    public void setTipoReporteId(Long tipoReporteId) { this.tipoReporteId = tipoReporteId; }
    public String getNombreTipoReporte() { return nombreTipoReporte; }
    public void setNombreTipoReporte(String nombreTipoReporte) { this.nombreTipoReporte = nombreTipoReporte; }
    public String getNombreArea() { return nombreArea; }
    public void setNombreArea(String nombreArea) { this.nombreArea = nombreArea; }
    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }
    public String getUidFuncionario() { return uidFuncionario; }
    public void setUidFuncionario(String uidFuncionario) { this.uidFuncionario = uidFuncionario; }
    public Long getClusterId() { return clusterId; }
    public void setClusterId(Long clusterId) { this.clusterId = clusterId; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public LocalDateTime getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDateTime fechaReporte) { this.fechaReporte = fechaReporte; }
}
