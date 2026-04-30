package com.example.microservicioreportes.dto;

import java.util.List;

public class ReportSummaryDTO {
    private long totalReportes;
    private TiemposDTO tiempos;
    private PorEstadoDTO porEstado;
    private double averageResolutionHours;
    private List<MapDataDTO> mapDataByZone;
    private List<MapDataDTO> mapDataByArea;

    // Getters y Setters
    public long getTotalReportes() { return totalReportes; }
    public void setTotalReportes(long totalReportes) { this.totalReportes = totalReportes; }

    public TiemposDTO getTiempos() { return tiempos; }
    public void setTiempos(TiemposDTO tiempos) { this.tiempos = tiempos; }

    public PorEstadoDTO getPorEstado() { return porEstado; }
    public void setPorEstado(PorEstadoDTO porEstado) { this.porEstado = porEstado; }

    public double getAverageResolutionHours() { return averageResolutionHours; }
    public void setAverageResolutionHours(double averageResolutionHours) { this.averageResolutionHours = averageResolutionHours; }

    public List<MapDataDTO> getMapDataByZone() { return mapDataByZone; }
    public void setMapDataByZone(List<MapDataDTO> mapDataByZone) { this.mapDataByZone = mapDataByZone; }

    public List<MapDataDTO> getMapDataByArea() { return mapDataByArea; }
    public void setMapDataByArea(List<MapDataDTO> mapDataByArea) { this.mapDataByArea = mapDataByArea; }

    // Clases internas
    public static class TiemposDTO {
        private long enProceso;
        private long enRevision;
        private long resueltos;
        private long pendientes;

        public long getEnProceso() { return enProceso; }
        public void setEnProceso(long enProceso) { this.enProceso = enProceso; }

        public long getEnRevision() { return enRevision; }
        public void setEnRevision(long enRevision) { this.enRevision = enRevision; }

        public long getResueltos() { return resueltos; }
        public void setResueltos(long resueltos) { this.resueltos = resueltos; }

        public long getPendientes() { return pendientes; }
        public void setPendientes(long pendientes) { this.pendientes = pendientes; }
    }

    public static class PorEstadoDTO {
        private long pendiente;
        private long en_revision;
        private long en_proceso;
        private long resuelto;

        public long getPendiente() { return pendiente; }
        public void setPendiente(long pendiente) { this.pendiente = pendiente; }

        public long getEn_revision() { return en_revision; }
        public void setEn_revision(long en_revision) { this.en_revision = en_revision; }

        public long getEn_proceso() { return en_proceso; }
        public void setEn_proceso(long en_proceso) { this.en_proceso = en_proceso; }

        public long getResuelto() { return resuelto; }
        public void setResuelto(long resuelto) { this.resuelto = resuelto; }
    }

    public static class MapDataDTO {
        private String id;
        private String label;
        private Double lat;
        private Double lng;
        private long total;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }

        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }

        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
    }
}