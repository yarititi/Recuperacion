package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    
    // ✅ NUEVO: Método nativo
    public List<CitaEntity> findByUsuarioIdNative(Long usuarioId) {
        return citaRepository.findByUsuarioIdNative(usuarioId);
    }
    
    // ✅ NUEVO: Método manual como fallback
    public List<CitaEntity> findByUsuarioIdManual(Long usuarioId) {
        List<CitaEntity> todasCitas = citaRepository.findAll();
        System.out.println("🔍 FILTRANDO " + todasCitas.size() + " CITAS MANUALMENTE PARA USUARIO: " + usuarioId);
        
        List<CitaEntity> citasFiltradas = todasCitas.stream()
                .filter(cita -> {
                    boolean coincide = cita.getUsuario() != null && cita.getUsuario().getId().equals(usuarioId);
                    if (coincide) {
                        System.out.println("✅ CITA COINCIDE - ID: " + cita.getId() + ", Usuario: " + cita.getUsuario().getId());
                    }
                    return coincide;
                })
                .sorted((c1, c2) -> c2.getFechaHora().compareTo(c1.getFechaHora()))
                .collect(Collectors.toList());
        
        System.out.println("📊 CITAS FILTRADAS MANUALMENTE: " + citasFiltradas.size());
        return citasFiltradas;
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
    
    // ✅ NUEVO: Método con debugging
    public CitaEntity saveWithDebug(CitaEntity cita) {
        System.out.println("💾 INTENTANDO GUARDAR CITA:");
        System.out.println("   👤 Usuario: " + (cita.getUsuario() != null ? cita.getUsuario().getId() + " - " + cita.getUsuario().getNombre() : "NULO"));
        System.out.println("   👨‍⚕️ Profesional: " + (cita.getProfesional() != null ? cita.getProfesional().getId() + " - " + cita.getProfesional().getUsuario().getNombre() : "NULO"));
        System.out.println("   🏥 Servicio: " + (cita.getServicio() != null ? cita.getServicio().getId() + " - " + cita.getServicio().getNombre() : "NULO"));
        System.out.println("   📅 FechaHora: " + cita.getFechaHora());
        System.out.println("   📊 Estado: " + cita.getEstado());
        
        CitaEntity citaGuardada = citaRepository.save(cita);
        
        System.out.println("✅ CITA GUARDADA - ID: " + citaGuardada.getId());
        return citaGuardada;
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