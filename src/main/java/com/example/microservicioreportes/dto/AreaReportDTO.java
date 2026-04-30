package com.example.microservicioreportes.dto;

public class AreaReportDTO {
    private String areaNombre;
    private long total;
    private double porcentaje;

    // Constructor vacío para Jackson
    public AreaReportDTO() {}

    // Constructor con parámetros
    public AreaReportDTO(String areaNombre, long total, double porcentaje) {
        this.areaNombre = areaNombre;
        this.total = total;
        this.porcentaje = porcentaje;
    }

    // Getters y Setters
    public String getAreaNombre() { return areaNombre; }
    public void setAreaNombre(String areaNombre) { this.areaNombre = areaNombre; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
}