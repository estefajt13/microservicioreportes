package com.example.microservicioreportes.service.client;

public class AnaliticaNuevoReporteDTO {

    private Long idReporte;
    private Long idTipoReporte;
    private String nombreTipoReporte;
    private String nombreArea;
    private Double latitud;
    private Double longitud;
    private String fechaReporte;

    public Long getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Long idReporte) {
        this.idReporte = idReporte;
    }

    public Long getIdTipoReporte() {
        return idTipoReporte;
    }

    public void setIdTipoReporte(Long idTipoReporte) {
        this.idTipoReporte = idTipoReporte;
    }

    public String getNombreTipoReporte() {
        return nombreTipoReporte;
    }

    public void setNombreTipoReporte(String nombreTipoReporte) {
        this.nombreTipoReporte = nombreTipoReporte;
    }

    public String getNombreArea() {
        return nombreArea;
    }

    public void setNombreArea(String nombreArea) {
        this.nombreArea = nombreArea;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getFechaReporte() {
        return fechaReporte;
    }

    public void setFechaReporte(String fechaReporte) {
        this.fechaReporte = fechaReporte;
    }
}
