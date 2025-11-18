package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Service.UsuarioService;
import com.clinica.sistema.Service.CitaService;
import com.clinica.sistema.Service.ProfesionalService;
import com.clinica.sistema.Service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CitaService citaService;

    @Autowired
    private ProfesionalService profesionalService;

    @Autowired
    private ServicioService servicioService;

    // 🔧 MÉTODO AUXILIAR
    private UsuarioEntity getUsuarioFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // 🏠 DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 ACCEDIENDO AL DASHBOARD USUARIO");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            List<CitaEntity> citas = citaService.findByUsuarioId(usuario.getId());
            
            // Estadísticas
            long totalCitas = citas.size();
            long citasPendientes = citas.stream()
                    .filter(c -> "PENDIENTE".equals(c.getEstado()))
                    .count();
            long citasConfirmadas = citas.stream()
                    .filter(c -> "CONFIRMADA".equals(c.getEstado()))
                    .count();
            long citasCompletadas = citas.stream()
                    .filter(c -> "COMPLETADA".equals(c.getEstado()))
                    .count();
            
            // Próximas citas
            List<CitaEntity> proximasCitas = citas.stream()
                    .filter(c -> c.getFechaHora().isAfter(LocalDateTime.now()))
                    .filter(c -> "PENDIENTE".equals(c.getEstado()) || "CONFIRMADA".equals(c.getEstado()))
                    .limit(5)
                    .collect(Collectors.toList());

            model.addAttribute("usuario", usuario);
            model.addAttribute("totalCitas", totalCitas);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            model.addAttribute("citasCompletadas", citasCompletadas);
            model.addAttribute("proximasCitas", proximasCitas);
            
            System.out.println("✅ DASHBOARD USUARIO CARGADO - " + totalCitas + " citas");
            return "user/dashboard";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN DASHBOARD USUARIO: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "user/dashboard";
        }
    }

    // 📅 MIS CITAS - LISTAR
    @GetMapping("/citas")
    public String listarCitas(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 LISTANDO CITAS DEL USUARIO");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            List<CitaEntity> citas = citaService.findByUsuarioId(usuario.getId());
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("citas", citas);
            System.out.println("✅ CITAS CARGADAS: " + citas.size() + " citas");
            return "user/citas/listar";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR LISTANDO CITAS: " + e.getMessage());
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            return "user/citas/listar";
        }
    }

    // 📅 AGENDAR CITA - FORMULARIO
    @GetMapping("/citas/nueva")
    public String nuevaCitaForm(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 FORMULARIO NUEVA CITA");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            List<ProfesionalEntity> profesionales = profesionalService.findAll();
            List<ServicioEntity> servicios = servicioService.findAll();
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("profesionales", profesionales);
            model.addAttribute("servicios", servicios);
            model.addAttribute("cita", new CitaEntity());
            
            System.out.println("✅ FORMULARIO CARGADO - " + profesionales.size() + " profesionales");
            return "user/citas/formulario";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN FORMULARIO CITA: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/usuario/citas";
        }
    }

    // 📅 AGENDAR CITA - GUARDAR
    @PostMapping("/citas/nueva")
    public String guardarCita(@ModelAttribute CitaEntity cita,
                            @RequestParam Long profesionalId,
                            @RequestParam Long servicioId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("💾 GUARDANDO NUEVA CITA");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            
            cita.setUsuario(usuario);
            cita.setProfesional(profesionalService.findById(profesionalId)
                    .orElseThrow(() -> new RuntimeException("Profesional no encontrado")));
            cita.setServicio(servicioService.findById(servicioId)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado")));
            cita.setEstado("PENDIENTE");
            
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita agendada exitosamente");
            System.out.println("✅ CITA GUARDADA: " + cita.getId());
            return "redirect:/usuario/citas";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR GUARDANDO CITA: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al agendar cita: " + e.getMessage());
            return "redirect:/usuario/citas/formulario";
        }
    }

    // 👀 VER DETALLE DE CITA
    @GetMapping("/citas/{id}")
    public String verCita(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            System.out.println("🎯 VIENDO DETALLE DE CITA: " + id);
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            CitaEntity cita = citaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            // Verificar que la cita pertenece al usuario
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("No tienes permiso para ver esta cita");
            }

            model.addAttribute("usuario", usuario);
            model.addAttribute("cita", cita);
            System.out.println("✅ DETALLE CITA CARGADO");
            return "user/citas/detalle";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR VIENDO CITA: " + e.getMessage());
            model.addAttribute("error", "Error al cargar la cita: " + e.getMessage());
            return "redirect:/usuario/citas";
        }
    }

    // ❌ CANCELAR CITA
    @PostMapping("/citas/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("❌ CANCELANDO CITA: " + id);
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            CitaEntity cita = citaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("No tienes permiso para cancelar esta cita");
            }

            cita.setEstado("CANCELADA");
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
            System.out.println("✅ CITA CANCELADA");
            return "redirect:/usuario/citas";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR CANCELANDO CITA: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
            return "redirect:/usuario/citas";
        }
    }

    // 👤 VER PERFIL
    @GetMapping("/perfil")
    public String verPerfil(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 VIENDO PERFIL USUARIO");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            
            List<CitaEntity> citas = citaService.findByUsuarioId(usuario.getId());
            long totalCitas = citas.size();
            long citasPendientes = citas.stream()
                    .filter(c -> "PENDIENTE".equals(c.getEstado()))
                    .count();
            long citasConfirmadas = citas.stream()
                    .filter(c -> "CONFIRMADA".equals(c.getEstado()))
                    .count();

            model.addAttribute("usuario", usuario);
            model.addAttribute("totalCitas", totalCitas);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            
            System.out.println("✅ PERFIL CARGADO");
            return "user/perfil/ver";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN PERFIL: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "redirect:/usuario/dashboard";
        }
    }

    // ✏️ EDITAR PERFIL - FORMULARIO
    @GetMapping("/perfil/editar")
    public String editarPerfilForm(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 EDITANDO PERFIL USUARIO");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            model.addAttribute("usuario", usuario);
            return "user/perfil/editar";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EDITANDO PERFIL: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/usuario/perfil";
        }
    }

    // 💾 ACTUALIZAR PERFIL
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                  @RequestParam String telefono,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            System.out.println("💾 ACTUALIZANDO PERFIL: " + nombre);
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            
            usuario.setNombre(nombre);
            usuario.setTelefono(telefono);
            usuarioService.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
            System.out.println("✅ PERFIL ACTUALIZADO");
            return "redirect:/usuario/perfil";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR ACTUALIZANDO PERFIL: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/usuario/perfil/editar";
        }
    }

    // 🔐 CAMBIAR CONTRASEÑA
    @GetMapping("/perfil/cambiar-password")
    public String cambiarPasswordForm(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 CAMBIANDO CONTRASEÑA");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            model.addAttribute("usuario", usuario);
            return "user/perfil/cambiar-password";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR CAMBIANDO CONTRASEÑA: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/usuario/perfil";
        }
    }
}