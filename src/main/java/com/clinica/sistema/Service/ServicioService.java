package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.Servicio;
import com.clinica.sistema.Repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {
    
    @Autowired
    private ServicioRepository servicioRepository;
    
    public List<Servicio> findAll() {
        return servicioRepository.findAll();
    }
    
    public Optional<Servicio> findById(Long id) {
        return servicioRepository.findById(id);
    }
    
    public Servicio save(Servicio servicio) {
        return servicioRepository.save(servicio);
    }
}