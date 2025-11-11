package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalService {
    
    @Autowired
    private ProfesionalRepository profesionalRepository;
    
    public List<ProfesionalEntity> findAll() {
        return profesionalRepository.findAll();
    }
    
    public Optional<ProfesionalEntity> findById(Long id) {
        return profesionalRepository.findById(id);
    }
    
    public Optional<ProfesionalEntity> findByUsuarioEmail(String email) {
        return profesionalRepository.findByUsuarioEmail(email);
    }
    
    public ProfesionalEntity save(ProfesionalEntity profesional) {
        return profesionalRepository.save(profesional);
    }
}