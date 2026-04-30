package com.example.microservicioreportes.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reporte_historial")
public class HistorialCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_reporte", nullable = false)
    private Reporte reporte;

    @Column(name = "uid_usuario", nullable = false)
    private String uidUsuario; // Quién hizo el cambio (funcionario)

    private String tipo; // "estado", "comentario_interno", "mensaje_ciudadano"

    private String descripcion; // "Cambió estado a: en_proceso"

    @Column(columnDefinition = "TEXT")
    private String comentario; // Comentario adicional

    @Column(name = "visible_ciudadano")
    private Boolean visibleCiudadano = false; // Si es mensaje al ciudadano

    @Column(name = "fecha_cambio")
    private LocalDateTime fechaCambio = LocalDateTime.now();

    // Getters y Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Reporte getReporte() { return reporte; }
    public void setReporte(Reporte reporte) { this.reporte = reporte; }

    public String getUidUsuario() { return uidUsuario; }
    public void setUidUsuario(String uidUsuario) { this.uidUsuario = uidUsuario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public Boolean getVisibleCiudadano() { return visibleCiudadano; }
    public void setVisibleCiudadano(Boolean visibleCiudadano) { this.visibleCiudadano = visibleCiudadano; }

    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public void setFechaCambio(LocalDateTime fechaCambio) { this.fechaCambio = fechaCambio; }
}