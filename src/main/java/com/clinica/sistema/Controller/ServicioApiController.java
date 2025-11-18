package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/servicios")
public class ServicioApiController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping
    public ResponseEntity<?> listarServicios(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Double minPrecio,
            @RequestParam(required = false) Double maxPrecio,
            @RequestParam(required = false) Boolean activo) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<ServicioEntity> servicios;
            
            if (nombre != null && !nombre.isEmpty()) {
                servicios = servicioService.findByNombreContaining(nombre);
            } else if (minPrecio != null && maxPrecio != null) {
                servicios = servicioService.findByPrecioBetween(minPrecio, maxPrecio);
            } else if (activo != null) {
                servicios = servicioService.findByActivo(activo);
            } else {
                servicios = servicioService.findAll();
            }
            
            response.put("success", true);
            response.put("data", servicios);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al listar servicios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerServicio(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            return servicioService.findById(id)
                .map(servicio -> {
                    response.put("success", true);
                    response.put("data", servicio);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Servicio no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al obtener el servicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearServicio(@RequestBody ServicioEntity servicio) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validaciones básicas
            if (servicio.getNombre() == null || servicio.getNombre().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "El nombre del servicio es obligatorio");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            if (servicio.getPrecio() == null || servicio.getPrecio() <= 0) {
                response.put("success", false);
                response.put("message", "El precio debe ser mayor a cero");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // Asegurar que el estado esté definido
            if (servicio.getActivo() == null) {
                servicio.setActivo(true);
            }
            
            ServicioEntity nuevoServicio = servicioService.save(servicio);
            
            response.put("success", true);
            response.put("message", "Servicio creado exitosamente");
            response.put("data", nuevoServicio);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al crear el servicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarServicio(
            @PathVariable Long id,
            @RequestBody ServicioEntity servicioActualizado) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            return servicioService.findById(id)
                .map(servicio -> {
                    // Actualizar solo los campos permitidos
                    if (servicioActualizado.getNombre() != null) {
                        servicio.setNombre(servicioActualizado.getNombre());
                    }
                    if (servicioActualizado.getDescripcion() != null) {
                        servicio.setDescripcion(servicioActualizado.getDescripcion());
                    }
                    if (servicioActualizado.getPrecio() != null && servicioActualizado.getPrecio() > 0) {
                        servicio.setPrecio(servicioActualizado.getPrecio());
                    }
                    if (servicioActualizado.getDuracion() != null) {
                        servicio.setDuracion(servicioActualizado.getDuracion());
                    }
                    if (servicioActualizado.getActivo() != null) {
                        servicio.setActivo(servicioActualizado.getActivo());
                    }
                    
                    ServicioEntity servicioActualizadoDB = servicioService.save(servicio);
                    
                    response.put("success", true);
                    response.put("message", "Servicio actualizado exitosamente");
                    response.put("data", servicioActualizadoDB);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Servicio no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al actualizar el servicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarServicio(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            return servicioService.findById(id)
                .map(servicio -> {
                    servicioService.deleteById(id);
                    response.put("success", true);
                    response.put("message", "Servicio eliminado exitosamente");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Servicio no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el servicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            return servicioService.findById(id)
                .map(servicio -> {
                    servicio.setActivo(activo);
                    ServicioEntity servicioActualizado = servicioService.save(servicio);
                    
                    response.put("success", true);
                    response.put("message", String.format("Servicio %s exitosamente", 
                        activo ? "activado" : "desactivado"));
                    response.put("data", servicioActualizado);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Servicio no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar el estado del servicio: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
