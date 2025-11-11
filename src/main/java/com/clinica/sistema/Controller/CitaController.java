package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Service.CitaService;
import com.clinica.sistema.Service.ServicioService;
import com.clinica.sistema.Service.UsuarioService;
import com.clinica.sistema.Service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/user/citas")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfesionalService profesionalService;

    // 📋 LISTAR TODAS LAS CITAS DEL USUARIO
    @GetMapping
    public String listarCitas(Authentication authentication, Model model) {
        String email = authentication.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<CitaEntity> citas = citaService.findByUsuarioId(usuario.getId());
        model.addAttribute("citas", citas);
        model.addAttribute("usuario", usuario);
        
        return "user/citas/listar";
    }

    // 📝 MOSTRAR FORMULARIO PARA NUEVA CITA (MODIFICADO)
    @GetMapping("/nueva")
    public String mostrarFormularioNuevaCita(
            @RequestParam(required = false) Long servicioId,
            Authentication authentication, 
            Model model) {
        
        String email = authentication.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        List<ServicioEntity> servicios = servicioService.findAll();
        List<ProfesionalEntity> profesionales = profesionalService.findAll();
        
        CitaEntity cita = new CitaEntity();
        
        // Si hay un servicio seleccionado, establecerlo
        if (servicioId != null) {
            ServicioEntity servicioSeleccionado = servicioService.findById(servicioId)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            cita.setServicio(servicioSeleccionado);
            model.addAttribute("servicioSeleccionado", servicioSeleccionado);
        }
        
        model.addAttribute("cita", cita);
        model.addAttribute("servicios", servicios);
        model.addAttribute("profesionales", profesionales);
        model.addAttribute("usuario", usuario);
        
        return "user/citas/formulario";
    }

    // 💾 GUARDAR NUEVA CITA (MODIFICADO)
    @PostMapping("/guardar")
    public String guardarCita(
            @ModelAttribute CitaEntity cita,
            @RequestParam String fecha,
            @RequestParam String hora,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        try {
            String email = authentication.getName();
            UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Combinar fecha y hora
            LocalDate fechaDate = LocalDate.parse(fecha);
            LocalTime horaTime = LocalTime.parse(hora);
            LocalDateTime fechaHoraCompleta = LocalDateTime.of(fechaDate, horaTime);
            cita.setFechaHora(fechaHoraCompleta);
            
            cita.setUsuario(usuario);
            cita.setEstado("PENDIENTE");
            
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita agendada exitosamente");
            return "redirect:/user/citas";
            
        } catch (Exception e) {
            // En caso de error, recargar el formulario con los datos
            String email = authentication.getName();
            UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            List<ServicioEntity> servicios = servicioService.findAll();
            List<ProfesionalEntity> profesionales = profesionalService.findAll();
            
            if (cita.getServicio() != null) {
                ServicioEntity servicioSeleccionado = servicioService.findById(cita.getServicio().getId())
                    .orElse(null);
                model.addAttribute("servicioSeleccionado", servicioSeleccionado);
            }
            
            model.addAttribute("cita", cita);
            model.addAttribute("servicios", servicios);
            model.addAttribute("profesionales", profesionales);
            model.addAttribute("usuario", usuario);
            model.addAttribute("error", "Error al agendar cita: " + e.getMessage());
            
            return "user/citas/formulario";
        }
    }

    // ✏️ MOSTRAR FORMULARIO PARA EDITAR CITA (MODIFICADO)
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditarCita(@PathVariable Long id, 
                                             Authentication authentication, 
                                             Model model) {
        String email = authentication.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        CitaEntity cita = citaService.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        // Verificar que la cita pertenece al usuario
        if (!cita.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para editar esta cita");
        }
        
        List<ServicioEntity> servicios = servicioService.findAll();
        List<ProfesionalEntity> profesionales = profesionalService.findAll();
        
        // Cargar servicio seleccionado para preview
        if (cita.getServicio() != null) {
            model.addAttribute("servicioSeleccionado", cita.getServicio());
        }
        
        model.addAttribute("cita", cita);
        model.addAttribute("servicios", servicios);
        model.addAttribute("profesionales", profesionales);
        model.addAttribute("usuario", usuario);
        
        return "user/citas/formulario";
    }

    // 👀 VER DETALLES DE UNA CITA
    @GetMapping("/{id}")
    public String verCita(@PathVariable Long id, Authentication authentication, Model model) {
        String email = authentication.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        CitaEntity cita = citaService.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        
        // Verificar que la cita pertenece al usuario
        if (!cita.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("No tienes permiso para ver esta cita");
        }
        
        model.addAttribute("cita", cita);
        return "user/citas/detalle";
    }

    // ❌ CANCELAR CITA
    @PostMapping("/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, 
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            CitaEntity cita = citaService.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            // Verificar que la cita pertenece al usuario
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("No tienes permiso para cancelar esta cita");
            }
            
            cita.setEstado("CANCELADA");
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
            return "redirect:/user/citas";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
            return "redirect:/user/citas";
        }
    }
}