package com.clinica.sistema.Entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String telefono;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    private String rol;
    
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private ProfesionalEntity profesional;
    
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<CitaEntity> citas;
    
    public UsuarioEntity() {
        this.fechaRegistro = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public ProfesionalEntity getProfesional() { return profesional; }
    public void setProfesional(ProfesionalEntity profesional) { this.profesional = profesional; }
    public List<CitaEntity> getCitas() { return citas; }
    public void setCitas(List<CitaEntity> citas) { this.citas = citas; }
}