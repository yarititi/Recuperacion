package com.clinica.sistema.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profesional")
public class ProfesionalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String especialidad;
    
    @Column(name = "horario_disponible")
    private LocalDateTime horarioDisponible;
    
    @OneToOne
    @JoinColumn(name = "usuario_id")
    private UsuarioEntity usuario;
    
    @OneToMany(mappedBy = "profesional", cascade = CascadeType.ALL)
    private List<CitaEntity> citas = new ArrayList<>();
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
    public LocalDateTime getHorarioDisponible() { return horarioDisponible; }
    public void setHorarioDisponible(LocalDateTime horarioDisponible) { this.horarioDisponible = horarioDisponible; }
    public UsuarioEntity getUsuario() { return usuario; }
    public void setUsuario(UsuarioEntity usuario) { this.usuario = usuario; }
    public List<CitaEntity> getCitas() { return citas; }
    public void setCitas(List<CitaEntity> citas) { this.citas = citas; }
}