package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.ProfesionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<ProfesionalEntity, Long> {
    
    // Buscar profesional por ID de usuario
    Optional<ProfesionalEntity> findByUsuarioId(Long usuarioId);
    
    // Buscar profesional por email de usuario
    @Query("SELECT p FROM ProfesionalEntity p WHERE p.usuario.email = :email")
    Optional<ProfesionalEntity> findByUsuarioEmail(String email);
    
    // Listar todos los profesionales con información de usuario
    @Query("SELECT p FROM ProfesionalEntity p JOIN FETCH p.usuario")
    List<ProfesionalEntity> findAllWithUsuario();
    
    // Buscar profesionales por especialidad
    List<ProfesionalEntity> findByEspecialidad(String especialidad);
}