package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.Profesional;
import com.clinica.sistema.Repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalService {
    
    @Autowired
    private ProfesionalRepository profesionalRepository;
    
    public List<Profesional> findAll() {
        return profesionalRepository.findAll();
    }
    
    public Optional<Profesional> findById(Long id) {
        return profesionalRepository.findById(id);
    }
    
    public Optional<Profesional> findByUsuarioEmail(String email) {
        return profesionalRepository.findByUsuarioEmail(email);
    }
    
    public Profesional save(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }
}