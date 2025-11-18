package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Repository.ServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServicioService {

    @Autowired
    private ServicioRepository servicioRepository;

    public List<ServicioEntity> findAll() {
        return servicioRepository.findAll();
    }

    public Optional<ServicioEntity> findById(Long id) {
        return servicioRepository.findById(id);
    }

    @Transactional
    public ServicioEntity save(ServicioEntity servicio) {
        return servicioRepository.save(servicio);
    }

    @Transactional
    public void deleteById(Long id) {
        servicioRepository.deleteById(id);
    }
    
    public boolean existsById(Long id) {
        return servicioRepository.existsById(id);
    }
    
    public List<ServicioEntity> findByNombreContaining(String nombre) {
        return servicioRepository.findByNombreContainingIgnoreCase(nombre);
    }
    
    public List<ServicioEntity> findByPrecioBetween(Double minPrecio, Double maxPrecio) {
        return servicioRepository.findByPrecioBetween(minPrecio, maxPrecio);
    }
    
    public List<ServicioEntity> findByActivo(boolean activo) {
        return servicioRepository.findByActivo(activo);
    }

    // Métodos de búsqueda
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

    // ✅ NUEVOS MÉTODOS PARA ADMIN
    public long count() {
        return servicioRepository.count();
    }
    
    public long countByActivo(Boolean activo) {
        return servicioRepository.findByActivo(activo).size();
    }
    
    public List<ServicioEntity> findByActivo(Boolean activo) {
        return servicioRepository.findByActivo(activo);
    }
    
    public List<ServicioEntity> findByCategoria(String categoria) {
        return servicioRepository.findByCategoria(categoria);
    }
}