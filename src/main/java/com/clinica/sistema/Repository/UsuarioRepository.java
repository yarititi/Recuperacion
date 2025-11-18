package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // ✅ NUEVOS MÉTODOS PARA ADMIN
    // Buscar usuarios por estado activo
    @Query("SELECT u FROM UsuarioEntity u WHERE u.activo = :activo")
    List<UsuarioEntity> findByActivo(@Param("activo") Boolean activo);
    
    // Buscar usuarios por rol y estado activo
    @Query("SELECT u FROM UsuarioEntity u WHERE u.rol = :rol AND u.activo = :activo")
    List<UsuarioEntity> findByRolAndActivo(@Param("rol") String rol, @Param("activo") Boolean activo);
    
    // Buscar profesionales (usuarios con rol PROFESIONAL)
    @Query("SELECT u FROM UsuarioEntity u WHERE u.rol = 'PROFESIONAL'")
    List<UsuarioEntity> findProfesionales();
}