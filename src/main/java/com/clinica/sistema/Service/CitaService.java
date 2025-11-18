package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    public List<CitaEntity> findAll() {
        return citaRepository.findAll();
    }

    public Optional<CitaEntity> findById(Long id) {
        return citaRepository.findById(id);
    }

    public List<CitaEntity> findByUsuarioId(Long usuarioId) {
        return citaRepository.findByUsuarioId(usuarioId);
    }

    public List<CitaEntity> findByProfesionalId(Long profesionalId) {
        return citaRepository.findByProfesionalId(profesionalId);
    }

    public List<CitaEntity> findByEstado(String estado) {
        return citaRepository.findByEstado(estado);
    }

    public CitaEntity save(CitaEntity cita) {
        return citaRepository.save(cita);
    }

    public void deleteById(Long id) {
        citaRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return citaRepository.existsById(id);
    }

    // ✅ NUEVOS MÉTODOS PARA ADMIN
    public long count() {
        return citaRepository.count();
    }
    
    public List<CitaEntity> findByFechaHoraBetween(LocalDateTime start, LocalDateTime end) {
        return citaRepository.findByFechaHoraBetween(start, end);
    }
    
    public long countCitasHoy() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
        return citaRepository.findByFechaHoraBetween(startOfDay, endOfDay).size();
    }
    
    public long countCitasEsteMes() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().getMonth().maxLength())
                .withHour(23).withMinute(59).withSecond(59);
        return citaRepository.findByFechaHoraBetween(startOfMonth, endOfMonth).size();
    }
}