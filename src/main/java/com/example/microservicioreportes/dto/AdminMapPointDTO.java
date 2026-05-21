package com.example.microservicioreportes.dto;

public class AdminMapPointDTO {
    private String id;
    private String label;
    private Double lat;
    private Double lng;
    private long total;
    private String kind;
    private Long clusterId;

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

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public Long getClusterId() { return clusterId; }
    public void setClusterId(Long clusterId) { this.clusterId = clusterId; }
}
