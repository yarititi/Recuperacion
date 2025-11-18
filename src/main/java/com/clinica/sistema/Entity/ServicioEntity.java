package com.clinica.sistema.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "servicio")
public class ServicioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private String descripcion;
    private String duracion;
    private Double precio;
    
    @Column(name = "categoria")
    private String categoria;
    
    @Column(name = "duracion_minutos")
    private Integer duracionMinutos = 30;
    
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
    
    @OneToMany(mappedBy = "servicio", cascade = CascadeType.ALL)
    private List<CitaEntity> citas;
    
    // Constructores
    public ServicioEntity() {
        this.activo = true;
        this.duracionMinutos = 30;
    }
    
    public ServicioEntity(String nombre, String descripcion, Double precio, String categoria) {
        this();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDuracion() { return duracion; }
    public void setDuracion(String duracion) { this.duracion = duracion; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(Integer duracionMinutos) { this.duracionMinutos = duracionMinutos; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    public List<CitaEntity> getCitas() { return citas; }
    public void setCitas(List<CitaEntity> citas) { this.citas = citas; }
}