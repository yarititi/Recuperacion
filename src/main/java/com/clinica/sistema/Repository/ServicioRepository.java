package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.ServicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<ServicioEntity, Long> {
    
    // Buscar servicios por nombre (búsqueda parcial)
    List<ServicioEntity> findByNombreContainingIgnoreCase(String nombre);
    
    // Buscar servicios por rango de precio
    List<ServicioEntity> findByPrecioBetween(Double precioMin, Double precioMax);
    
    // Ordenar servicios por precio ascendente
    List<ServicioEntity> findAllByOrderByPrecioAsc();
    
    // Ordenar servicios por precio descendente
    List<ServicioEntity> findAllByOrderByPrecioDesc();
    
   
    
    
}