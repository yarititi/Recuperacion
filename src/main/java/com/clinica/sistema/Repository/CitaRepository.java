package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<CitaEntity, Long> {
    
    // Encontrar citas por usuario
    List<CitaEntity> findByUsuarioId(Long usuarioId);
    
    // Encontrar citas por profesional
    List<CitaEntity> findByProfesionalId(Long profesionalId);
    
    // Encontrar citas por estado
    List<CitaEntity> findByEstado(String estado);
    
    // Encontrar citas por fecha range
    List<CitaEntity> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end);
    
    // Verificar disponibilidad de profesional en fecha/hora
    @Query("SELECT COUNT(c) > 0 FROM CitaEntity c WHERE c.profesional.id = :profesionalId AND c.fechaHora = :fechaHora AND c.estado != 'CANCELADA'")
    boolean existsByProfesionalAndFechaHora(@Param("profesionalId") Long profesionalId, 
                                           @Param("fechaHora") LocalDateTime fechaHora);
    
    // Encontrar citas pendientes de un usuario
    @Query("SELECT c FROM CitaEntity c WHERE c.usuario.id = :usuarioId AND c.estado = 'PENDIENTE' ORDER BY c.fechaHora ASC")
    List<CitaEntity> findPendientesByUsuarioId(@Param("usuarioId") Long usuarioId);
}