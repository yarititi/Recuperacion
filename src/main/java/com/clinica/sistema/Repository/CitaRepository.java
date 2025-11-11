package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.CitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<CitaEntity, Long> {
    
    // Buscar citas por ID de usuario
    List<CitaEntity> findByUsuarioId(Long usuarioId);
    
    // Buscar citas por ID de profesional
    List<CitaEntity> findByProfesionalId(Long profesionalId);
    
    // Buscar citas por estado
    List<CitaEntity> findByEstado(String estado);
    
    // Buscar citas por fecha
    List<CitaEntity> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end);
    
    // Buscar citas de un usuario con toda la información relacionada
    @Query("SELECT c FROM CitaEntity c " +
           "JOIN FETCH c.servicio " +
           "JOIN FETCH c.profesional p " +
           "JOIN FETCH p.usuario " +
           "WHERE c.usuario.id = :usuarioId")
    List<CitaEntity> findByUsuarioIdWithDetails(Long usuarioId);
    
    // Buscar citas de un profesional con toda la información relacionada
    @Query("SELECT c FROM CitaEntity c " +
           "JOIN FETCH c.servicio " +
           "JOIN FETCH c.usuario " +
           "WHERE c.profesional.id = :profesionalId")
    List<CitaEntity> findByProfesionalIdWithDetails(Long profesionalId);
    
    // Contar citas por estado
    @Query("SELECT c.estado, COUNT(c) FROM CitaEntity c GROUP BY c.estado")
    List<Object[]> countCitasByEstado();
    
    // Buscar citas pendientes para hoy
    @Query("SELECT c FROM CitaEntity c WHERE DATE(c.fechaHora) = CURRENT_DATE AND c.estado = 'PENDIENTE'")
    List<CitaEntity> findCitasPendientesHoy();
}