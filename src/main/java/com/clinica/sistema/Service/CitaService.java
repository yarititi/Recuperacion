package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}