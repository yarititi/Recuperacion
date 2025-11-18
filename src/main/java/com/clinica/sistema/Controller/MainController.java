package com.clinica.sistema.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.clinica.sistema.Repository.CitaRepository;
import com.clinica.sistema.Repository.ServicioRepository;
import com.clinica.sistema.Repository.UsuarioRepository;

import java.util.HashMap;
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
            model.addAttribute("servicios", servicioRepository.findAll());
            
        } catch (Exception e) {
            // Si hay error, usar datos de ejemplo
            stats.put("pacientes", 156);
            stats.put("profesionales", 23);
            stats.put("servicios", 8);
            stats.put("citas", 489);
            
            // Solo pasar lista vacía de servicios
            model.addAttribute("servicios", java.util.Collections.emptyList());
        }
        
        model.addAttribute("stats", stats);
        return "index";
    }
}