package com.example.microservicioreportes.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipos_reporte")
public class TipoReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String icono;

    @ManyToOne
    @JoinColumn(name = "id_area", nullable = false)
    private Area area;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getIcono() { return icono; }
    public void setIcono(String icono) { this.icono = icono; }

    public Area getArea() { return area; }
    public void setArea(Area area) { this.area = area; }
}