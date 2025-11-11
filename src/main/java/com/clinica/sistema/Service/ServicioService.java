package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    public List<ServicioEntity> findAll() {
        return servicioRepository.findAll();
    }
    
    public Optional<ServicioEntity> findById(Long id) {
        return servicioRepository.findById(id);
    }
    
    public ServicioEntity save(ServicioEntity servicio) {
        return servicioRepository.save(servicio);
    }
}