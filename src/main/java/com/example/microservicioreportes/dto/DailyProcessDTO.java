package com.example.microservicioreportes.dto;

public class DailyProcessDTO {
    private String date;
    private long total;
    private long creados;

    // Constructor vacío para Jackson
    public DailyProcessDTO() {}

    // Constructor con parámetros
    public DailyProcessDTO(String date, long total) {
        this.date = date;
        this.total = total;
        this.creados = total; // Por defecto, creados es igual a total
    }

    // Getters y Setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getCreados() { return creados; }
    public void setCreados(long creados) { this.creados = creados; }
}