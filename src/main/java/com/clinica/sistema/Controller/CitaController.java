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
import java.util.Optional;

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
    @GetMapping("/listar")
    public String listarCitas(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                model.addAttribute("error", "Usuario no encontrado");
                return "user/citas/listar";
            }

            UsuarioEntity usuario = usuarioOpt.get();
            List<CitaEntity> citas = citaService.findByUsuarioId(usuario.getId());
            
            // Asegurar que el token CSRF esté disponible en la vista
            model.addAttribute("_csrf", new org.springframework.security.web.csrf.CsrfToken() {
                @Override
                public String getHeaderName() {
                    return "X-CSRF-TOKEN";
                }
                @Override
                public String getParameterName() {
                    return "_csrf";
                }
                @Override
                public String getToken() {
                    return ""; // El token real se generará automáticamente
                }
            });
            
            model.addAttribute("citas", citas);
            model.addAttribute("usuario", usuario);
            return "user/citas/listar";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            return "user/citas/listar";
        }
    }

    // 📝 FORMULARIO PARA NUEVA CITA
    @GetMapping("/nueva")
    public String nuevaCita(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                return "redirect:/auth/login?error";
            }

            // Cargar servicios y profesionales
            List<ServicioEntity> servicios = servicioService.findAll();
            List<ProfesionalEntity> profesionales = profesionalService.findAll();

            // Crear nueva cita vacía
            CitaEntity cita = new CitaEntity();
            
            model.addAttribute("cita", cita);
            model.addAttribute("servicios", servicios);
            model.addAttribute("profesionales", profesionales);
            model.addAttribute("usuario", usuarioOpt.get());

            return "user/citas/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "user/citas/formulario";
        }
    }

    // 📝 FORMULARIO PARA EDITAR CITA
    @GetMapping("/editar/{id}")
    public String editarCita(@PathVariable Long id, 
                           Authentication authentication, 
                           Model model) {
        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                return "redirect:/auth/login?error";
            }

            UsuarioEntity usuario = usuarioOpt.get();
            Optional<CitaEntity> citaOpt = citaService.findById(id);
            
            if (citaOpt.isEmpty()) {
                model.addAttribute("error", "Cita no encontrada");
                return "redirect:/user/citas/listar";
            }

            CitaEntity cita = citaOpt.get();

            // Verificar que la cita pertenece al usuario
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                model.addAttribute("error", "No tienes permiso para editar esta cita");
                return "redirect:/user/citas/listar";
            }

            // Asegurarse de que el estado esté en mayúsculas
            if (cita.getEstado() != null) {
                cita.setEstado(cita.getEstado().trim().toUpperCase());
            } else {
                cita.setEstado("PENDIENTE");
            }

            System.out.println("Estado de la cita en editar: " + cita.getEstado());
            System.out.println("¿Es igual a 'CANCELADA'? " + "CANCELADA".equals(cita.getEstado()));

            // Cargar servicios y profesionales
            List<ServicioEntity> servicios = servicioService.findAll();
            List<ProfesionalEntity> profesionales = profesionalService.findAll();

            model.addAttribute("cita", cita);
            model.addAttribute("servicios", servicios);
            model.addAttribute("profesionales", profesionales);
            model.addAttribute("usuario", usuario);

            return "user/citas/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar la cita: " + e.getMessage());
            return "redirect:/user/citas/listar";
        }
    }

    // 👀 VER DETALLES DE UNA CITA
    @GetMapping("/detalle/{id}")
    public String verCita(@PathVariable Long id, 
                        Authentication authentication, 
                        Model model) {
        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                return "redirect:/auth/login?error";
            }

            UsuarioEntity usuario = usuarioOpt.get();
            Optional<CitaEntity> citaOpt = citaService.findById(id);
            
            if (citaOpt.isEmpty()) {
                model.addAttribute("error", "Cita no encontrada");
                return "redirect:/user/citas/listar";
            }

            CitaEntity cita = citaOpt.get();

            // Asegurarse de que el estado esté en mayúsculas
            if (cita.getEstado() != null) {
                cita.setEstado(cita.getEstado().trim().toUpperCase());
            } else {
                cita.setEstado("PENDIENTE");
            }
            
            // Debug: Imprimir el estado actualizado
            System.out.println("🔍 Estado después de normalización: '" + cita.getEstado() + "'");
            System.out.println("🔍 Longitud del estado: " + cita.getEstado().length());
            System.out.println("🔍 ¿Es igual a 'CANCELADA'? " + "CANCELADA".equals(cita.getEstado()));

            // Debug logging
            System.out.println("\n🔍 === INICIO DE DEPURACIÓN ===");
            System.out.println("🔍 Verificando cita ID: " + cita.getId());
            System.out.println("🔍 Estado de la cita: '" + cita.getEstado() + "'");
            System.out.println("🔍 Longitud del estado: " + (cita.getEstado() != null ? cita.getEstado().length() : 0));
            System.out.println("🔍 Estado en mayúsculas: '" + cita.getEstado().toUpperCase() + "'");
            System.out.println("🔍 ¿Es igual a 'CANCELADA'? " + ("CANCELADA".equals(cita.getEstado())));
            System.out.println("🔍 Usuario dueño: " + cita.getUsuario().getId());
            System.out.println("🔍 Usuario logueado: " + usuario.getId());
            System.out.println("🔍 ¿Puede cancelar? " + (!"CANCELADA".equals(cita.getEstado())));
            System.out.println("🔍 === FIN DE DEPURACIÓN ===\n");

            // Verificar que la cita pertenece al usuario
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                model.addAttribute("error", "No tienes permiso para ver esta cita");
                return "redirect:/user/citas/listar";
            }

            model.addAttribute("cita", cita);
            model.addAttribute("usuario", usuario);
            return "user/citas/detalle";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar los detalles: " + e.getMessage());
            return "redirect:/user/citas/listar";
        }
    }

    // 💾 GUARDAR O ACTUALIZAR CITA
    @PostMapping("/guardar")
    public String guardarCita(@RequestParam(required = false) Long id,
                            @RequestParam Long servicioId,
                            @RequestParam Long profesionalId, 
                            @RequestParam String fecha, 
                            @RequestParam String hora,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/auth/login";
            }

            UsuarioEntity usuario = usuarioOpt.get();

            // Validar parámetros requeridos
            if (servicioId == null || profesionalId == null || fecha == null || hora == null) {
                redirectAttributes.addFlashAttribute("error", "Todos los campos obligatorios deben ser completados");
                return "redirect:/user/citas/nueva";
            }

            // Obtener servicio y profesional
            Optional<ServicioEntity> servicioOpt = servicioService.findById(servicioId);
            Optional<ProfesionalEntity> profesionalOpt = profesionalService.findById(profesionalId);
            
            if (servicioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Servicio no encontrado");
                return "redirect:/user/citas/nueva";
            }
            
            if (profesionalOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Profesional no encontrado");
                return "redirect:/user/citas/nueva";
            }

            ServicioEntity servicioEntity = servicioOpt.get();
            ProfesionalEntity profesionalEntity = profesionalOpt.get();

            // Combinar fecha y hora
            LocalDate fechaDate = LocalDate.parse(fecha);
            LocalTime horaTime = LocalTime.parse(hora);
            LocalDateTime fechaHoraCompleta = LocalDateTime.of(fechaDate, horaTime);

            CitaEntity cita;

            if (id != null) {
                // MODO EDICIÓN
                Optional<CitaEntity> citaExistenteOpt = citaService.findById(id);
                if (citaExistenteOpt.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                    return "redirect:/user/citas/listar";
                }

                cita = citaExistenteOpt.get();

                // Verificar permisos
                if (!cita.getUsuario().getId().equals(usuario.getId())) {
                    redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta cita");
                    return "redirect:/user/citas/listar";
                }

                // Actualizar cita existente
                cita.setFechaHora(fechaHoraCompleta);
                cita.setServicio(servicioEntity);
                cita.setProfesional(profesionalEntity);

                citaService.save(cita);
                redirectAttributes.addFlashAttribute("success", "Cita actualizada exitosamente");
                
            } else {
                // MODO NUEVA CITA
                cita = new CitaEntity();
                cita.setFechaHora(fechaHoraCompleta);
                cita.setUsuario(usuario);
                cita.setServicio(servicioEntity);
                cita.setProfesional(profesionalEntity);
                // El estado "PENDIENTE" se asigna automáticamente en el constructor

                citaService.save(cita);
                redirectAttributes.addFlashAttribute("success", "Cita agendada exitosamente");
            }

            return "redirect:/user/citas/listar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la cita: " + e.getMessage());
            return "redirect:/user/citas/nueva";
        }
    }

    // ❌ CANCELAR CITA
    @PostMapping("/cancelar/{id}")
    public String cancelarCita(@PathVariable Long id, 
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/auth/login";
            }

            UsuarioEntity usuario = usuarioOpt.get();
            Optional<CitaEntity> citaOpt = citaService.findById(id);
            
            if (citaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                return "redirect:/user/citas/listar";
            }

            CitaEntity cita = citaOpt.get();

            // Verificar que la cita pertenece al usuario
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para cancelar esta cita");
                return "redirect:/user/citas/listar";
            }

            // Solo cancelar si está pendiente o confirmada
            if ("PENDIENTE".equals(cita.getEstado()) || "CONFIRMADA".equals(cita.getEstado())) {
                cita.setEstado("CANCELADA");
                citaService.save(cita);
                redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "No se puede cancelar una cita " + cita.getEstado());
            }

            return "redirect:/user/citas/listar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
            return "redirect:/user/citas/listar";
        }
    }

    // ✅ CONFIRMAR CITA (opcional - para el profesional)
    @PostMapping("/confirmar/{id}")
    public String confirmarCita(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            // Solo para profesionales/admin
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/auth/login";
            }

            Optional<CitaEntity> citaOpt = citaService.findById(id);
            if (citaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                return "redirect:/user/citas/listar";
            }

            CitaEntity cita = citaOpt.get();
            
            if ("PENDIENTE".equals(cita.getEstado())) {
                cita.setEstado("CONFIRMADA");
                citaService.save(cita);
                redirectAttributes.addFlashAttribute("success", "Cita confirmada exitosamente");
            } else {
                redirectAttributes.addFlashAttribute("error", "Solo se pueden confirmar citas pendientes");
            }

            return "redirect:/user/citas/listar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al confirmar cita: " + e.getMessage());
            return "redirect:/user/citas/listar";
        }
    }

    // ELIMINAR CITA (solo si está cancelada)
    @PostMapping("/eliminar/{id}")
    public String eliminarCita(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);
            
            if (usuarioOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
                return "redirect:/auth/login";
            }

            UsuarioEntity usuario = usuarioOpt.get();
            Optional<CitaEntity> citaOpt = citaService.findById(id);
            
            if (citaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                return "redirect:/user/citas/listar";
            }

            CitaEntity cita = citaOpt.get();

            // Verificar que la cita pertenece al usuario
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta cita");
                return "redirect:/user/citas/listar";
            }

            // Solo permitir eliminar si está cancelada
            if (!"CANCELADA".equals(cita.getEstado())) {
                redirectAttributes.addFlashAttribute("error", "Solo se pueden eliminar citas canceladas");
                return "redirect:/user/citas/" + id;
            }

            // Eliminar la cita
            citaService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Cita eliminada exitosamente");
            return "redirect:/user/citas/listar";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la cita: " + e.getMessage());
            return "redirect:/user/citas/" + id;
        }
    }
}