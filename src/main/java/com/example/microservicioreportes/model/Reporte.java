package com.example.microservicioreportes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uid_ciudadano", nullable = false)
    private String uidCiudadano;

    @ManyToOne
    @JoinColumn(name = "id_tipo", nullable = false)
    private TipoReporte tipoReporte;

    private String asunto;
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoReporte estado = EstadoReporte.pendiente;

    private String prioridad;
    private Double latitud;
    private Double longitud;
    private String direccion;
    private String zona;

    @Column(name = "uid_funcionario")
    private String uidFuncionario;

    private Boolean activo = true;

    @Column(name = "fecha_reporte")
    private LocalDateTime fechaReporte = LocalDateTime.now();

    // ── Getters y Setters ──────────────────────

    public Long getId() { return id; }

    public String getUidCiudadano() { return uidCiudadano; }
    public void setUidCiudadano(String u) { this.uidCiudadano = u; }

    public TipoReporte getTipoReporte() { return tipoReporte; }
    public void setTipoReporte(TipoReporte t) { this.tipoReporte = t; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String a) { this.asunto = a; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }

    public EstadoReporte getEstado() { return estado; }
    public void setEstado(EstadoReporte e) { this.estado = e; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String p) { this.prioridad = p; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double lat) { this.latitud = lat; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double lon) { this.longitud = lon; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String d) { this.direccion = d; }

    public String getZona() { return zona; }
    public void setZona(String z) { this.zona = z; }

    public String getUidFuncionario() { return uidFuncionario; }
    public void setUidFuncionario(String u) { this.uidFuncionario = u; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean a) { this.activo = a; }

    public LocalDateTime getFechaReporte() { return fechaReporte; }
    public void setFechaReporte(LocalDateTime f) { this.fechaReporte = f; }
}