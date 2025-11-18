package com.clinica.sistema.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cita")
public class CitaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;
    
    private String estado;
    
    // ✅ AGREGAR ESTE CAMPO
    private String motivo;
    
    @Column(columnDefinition = "TEXT")
    private String notas;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id")
    private ServicioEntity servicio;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesional_id")
    private ProfesionalEntity profesional;
    
    // Constructores
    public CitaEntity() {
        this.estado = "PENDIENTE";
    }
    
    public CitaEntity(LocalDateTime fechaHora, UsuarioEntity usuario, ServicioEntity servicio, ProfesionalEntity profesional) {
        this();
        this.fechaHora = fechaHora;
        this.usuario = usuario;
        this.servicio = servicio;
        this.profesional = profesional;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // ✅ AGREGAR GETTER Y SETTER PARA MOTIVO
    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public UsuarioEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    public ServicioEntity getServicio() {
        return servicio;
    }

    public void setServicio(ServicioEntity servicio) {
        this.servicio = servicio;
    }

    public ProfesionalEntity getProfesional() {
        return profesional;
    }

    public void setProfesional(ProfesionalEntity profesional) {
        this.profesional = profesional;
    }
}