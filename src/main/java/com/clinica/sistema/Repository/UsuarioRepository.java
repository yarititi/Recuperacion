package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    
    // Buscar usuario por email
    Optional<UsuarioEntity> findByEmail(String email);
    
    // Verificar si existe un usuario por email
    boolean existsByEmail(String email);
    
    // Buscar usuarios por rol
    List<UsuarioEntity> findByRol(String rol);
    
    // Buscar profesionales (usuarios con rol PROFESIONAL)
    @Query("SELECT u FROM UsuarioEntity u WHERE u.rol = 'PROFESIONAL'")
    List<UsuarioEntity> findProfesionales();
}