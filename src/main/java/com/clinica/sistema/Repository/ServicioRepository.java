package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.ServicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    // ✅ NUEVOS MÉTODOS PARA ADMIN
    // Buscar servicios por estado activo
    @Query("SELECT s FROM ServicioEntity s WHERE s.activo = :activo")
    List<ServicioEntity> findByActivo(@Param("activo") Boolean activo);
    
    // Buscar servicios por categoría
    @Query("SELECT s FROM ServicioEntity s WHERE s.categoria = :categoria")
    List<ServicioEntity> findByCategoria(@Param("categoria") String categoria);
    
    // Buscar servicios activos por categoría
    @Query("SELECT s FROM ServicioEntity s WHERE s.categoria = :categoria AND s.activo = true")
    List<ServicioEntity> findByCategoriaAndActivo(@Param("categoria") String categoria);
}