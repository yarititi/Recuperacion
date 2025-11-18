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
    
    // ✅ OPCIÓN 1: Consulta JPQL explícita (usa el nombre de la ENTIDAD)
    @Query("SELECT c FROM CitaEntity c WHERE c.usuario.id = :usuarioId ORDER BY c.fechaHora DESC")
    List<CitaEntity> findByUsuarioId(@Param("usuarioId") Long usuarioId);
    
    // ✅ OPCIÓN 2: Consulta nativa SQL con el nombre CORRECTO de la TABLA
    @Query(value = "SELECT * FROM cita WHERE usuario_id = :usuarioId ORDER BY fecha_hora DESC", nativeQuery = true)
    List<CitaEntity> findByUsuarioIdNative(@Param("usuarioId") Long usuarioId);
    
    // ✅ CORREGIDO: Consulta por profesional
    @Query("SELECT c FROM CitaEntity c WHERE c.profesional.id = :profesionalId")
    List<CitaEntity> findByProfesionalId(@Param("profesionalId") Long profesionalId);
    
    // Encontrar citas por estado
    List<CitaEntity> findByEstado(String estado);
    
    // Encontrar citas por fecha range
    List<CitaEntity> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end);
    
    // ✅ CORREGIDO: Verificar disponibilidad de profesional en fecha/hora
    @Query("SELECT COUNT(c) > 0 FROM CitaEntity c WHERE c.profesional.id = :profesionalId AND c.fechaHora = :fechaHora AND c.estado != 'CANCELADA'")
    boolean existsByProfesionalAndFechaHora(@Param("profesionalId") Long profesionalId, 
                                           @Param("fechaHora") LocalDateTime fechaHora);
    
    // ✅ CORREGIDO: Encontrar citas pendientes de un usuario
    @Query("SELECT c FROM CitaEntity c WHERE c.usuario.id = :usuarioId AND c.estado = 'PENDIENTE' ORDER BY c.fechaHora ASC")
    List<CitaEntity> findPendientesByUsuarioId(@Param("usuarioId") Long usuarioId);
}