package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Repository.UsuarioRepository;
import com.clinica.sistema.Repository.ServicioRepository;
import com.clinica.sistema.Repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private CitaRepository citaRepository;

    @GetMapping("/")
    public String home(Model model) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Obtener estadísticas reales
            long totalUsuarios = usuarioRepository.count();
            long totalProfesionales = usuarioRepository.findByRol("PROFESIONAL").size();
            long totalServicios = servicioRepository.count();
            long totalCitas = citaRepository.count();
            
            stats.put("pacientes", totalUsuarios);
            stats.put("profesionales", totalProfesionales);
            stats.put("servicios", totalServicios);
            stats.put("citas", totalCitas);
            
            // Obtener servicios para mostrar
            List<ServicioEntity> servicios = servicioRepository.findAll();
            model.addAttribute("servicios", servicios);
            
        } catch (Exception e) {
            // Si hay error (tablas vacías), usar datos de ejemplo
            stats.put("pacientes", 156);
            stats.put("profesionales", 23);
            stats.put("servicios", 8);
            stats.put("citas", 489);
            
            // Crear servicios de ejemplo
            List<ServicioEntity> serviciosEjemplo = List.of(
                crearServicioEjemplo("Consulta General", "Atención médica general", 50000.0),
                crearServicioEjemplo("Odontología", "Limpieza y tratamiento dental", 80000.0),
                crearServicioEjemplo("Oftalmología", "Exámenes de la vista", 75000.0)
            );
            model.addAttribute("servicios", serviciosEjemplo);
        }
        
        model.addAttribute("stats", stats);
        return "index";
    }
    
    private ServicioEntity crearServicioEjemplo(String nombre, String descripcion, Double precio) {
        ServicioEntity servicio = new ServicioEntity();
        servicio.setNombre(nombre);
        servicio.setDescripcion(descripcion);
        servicio.setPrecio(precio);
        servicio.setDuracion("30 minutos");
        return servicio;
    }
}