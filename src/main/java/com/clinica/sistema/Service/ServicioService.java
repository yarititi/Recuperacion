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

    public void deleteById(Long id) {
        servicioRepository.deleteById(id);
    }

    // Método opcional para buscar por nombre
    public List<ServicioEntity> buscarPorNombre(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<ServicioEntity> buscarPorRangoPrecio(Double min, Double max) {
        return servicioRepository.findByPrecioBetween(min, max);
    }
    
    public List<ServicioEntity> ordenarPorPrecioAsc() {
        return servicioRepository.findAllByOrderByPrecioAsc();
    }
    
    public List<ServicioEntity> ordenarPorPrecioDesc() {
        return servicioRepository.findAllByOrderByPrecioDesc();
    }
}