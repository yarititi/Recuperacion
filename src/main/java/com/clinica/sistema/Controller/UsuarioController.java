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

import jakarta.servlet.http.HttpServletRequest;
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
            List<CitaEntity> citas = citaService.findByUsuarioIdManual(usuario.getId()); // Usar método manual
            
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

    // 📅 MIS CITAS - LISTAR (CON DEBUGGING COMPLETO)
    @GetMapping("/citas")
    public String listarCitas(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 LISTANDO CITAS DEL USUARIO - DEBUGGING COMPLETO");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            System.out.println("👤 Usuario ID: " + usuario.getId() + ", Email: " + usuario.getEmail());
            
            // ✅ MÉTODO 3: Obtener todas y filtrar manualmente (MÁS CONFIABLE)
            List<CitaEntity> todasLasCitas = citaService.findAll();
            System.out.println("📊 TOTAL CITAS EN BD: " + todasLasCitas.size());
            
            List<CitaEntity> citasManual = citaService.findByUsuarioIdManual(usuario.getId());
            
            // ✅ LOG DETALLADO DE TODAS LAS CITAS EN BD
            System.out.println("--- TODAS LAS CITAS EN BD ---");
            for (CitaEntity cita : todasLasCitas) {
                System.out.println("📅 Cita ID: " + cita.getId() + 
                                 ", Fecha: " + cita.getFechaHora() + 
                                 ", Estado: " + cita.getEstado() +
                                 ", Usuario ID: " + (cita.getUsuario() != null ? cita.getUsuario().getId() : "NULO") +
                                 ", Usuario Nombre: " + (cita.getUsuario() != null ? cita.getUsuario().getNombre() : "NULO"));
            }
            System.out.println("--- FIN CITAS BD ---");
            
            // ✅ AÑADIR USUARIO AL MODELO (ESTO ES LO QUE FALTABA)
            model.addAttribute("usuario", usuario);
            model.addAttribute("citas", citasManual); // ← Usar las citas manuales
            
            if (citasManual.isEmpty()) {
                System.out.println("ℹ️ No se encontraron citas para el usuario " + usuario.getId());
                model.addAttribute("info", "No tienes citas agendadas.");
            } else {
                System.out.println("✅ Se encontraron " + citasManual.size() + " citas para el usuario");
            }
            
            return "user/citas/listar";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR LISTANDO CITAS: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            return "user/citas/listar";
        }
    }

    // 📅 AGENDAR CITA - FORMULARIO (NUEVA CITA)
    @GetMapping("/citas/nueva")
    public String nuevaCitaForm(Authentication authentication, Model model, HttpServletRequest request) {
        try {
            System.out.println("🎯 FORMULARIO NUEVA CITA");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            List<ProfesionalEntity> profesionales = profesionalService.findAll();
            List<ServicioEntity> servicios = servicioService.findAll();
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("profesionales", profesionales);
            model.addAttribute("servicios", servicios);
            model.addAttribute("cita", new CitaEntity());
            
            System.out.println("✅ FORMULARIO CARGADO - " + profesionales.size() + " profesionales, " + servicios.size() + " servicios");
            return "user/citas/formulario";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN FORMULARIO CITA: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/usuario/citas";
        }
    }

    // 📅 EDITAR CITA - FORMULARIO (CITA EXISTENTE)
    @GetMapping("/citas/editar/{id}")
    public String editarCitaForm(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            System.out.println("🎯 FORMULARIO EDITAR CITA: " + id);
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            
            // ✅ OBTENER CITA EXISTENTE
            CitaEntity cita = citaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            // Verificar que la cita pertenece al usuario
            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("No tienes permiso para editar esta cita");
            }
            
            List<ProfesionalEntity> profesionales = profesionalService.findAll();
            List<ServicioEntity> servicios = servicioService.findAll();
            
            model.addAttribute("usuario", usuario);
            model.addAttribute("profesionales", profesionales);
            model.addAttribute("servicios", servicios);
            model.addAttribute("cita", cita); // ← Cita existente para editar
            
            System.out.println("✅ FORMULARIO EDICIÓN CARGADO - Cita ID: " + cita.getId());
            return "user/citas/formulario";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN FORMULARIO EDICIÓN: " + e.getMessage());
            return "redirect:/usuario/citas";
        }
    }

    // 📅 AGENDAR CITA - GUARDAR (SOLO PARA NUEVAS CITAS)
    @PostMapping("/citas/nueva")
    public String guardarCita(@RequestParam Long profesionalId,
                            @RequestParam Long servicioId,
                            @RequestParam String fecha,
                            @RequestParam String hora,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            System.out.println("💾 CREANDO NUEVA CITA");
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            
            // ✅ VERIFICAR QUE EXISTEN LOS OBJETOS
            ProfesionalEntity profesional = profesionalService.findById(profesionalId)
                    .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
            
            ServicioEntity servicio = servicioService.findById(servicioId)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            
            // ✅ COMBINAR FECHA Y HORA
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora);
            
            // ✅ CREAR NUEVA CITA
            CitaEntity nuevaCita = new CitaEntity();
            nuevaCita.setUsuario(usuario);
            nuevaCita.setProfesional(profesional);
            nuevaCita.setServicio(servicio);
            nuevaCita.setFechaHora(fechaHora);
            nuevaCita.setEstado("PENDIENTE");
            
            // ✅ GUARDAR LA NUEVA CITA
            CitaEntity citaGuardada = citaService.save(nuevaCita);
            
            redirectAttributes.addFlashAttribute("success", 
                "Cita agendada exitosamente para el " + fecha + " a las " + hora);
            return "redirect:/usuario/citas";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR CREANDO CITA: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al agendar cita: " + e.getMessage());
            return "redirect:/usuario/citas/nueva";
        }
    }

    // 📅 ACTUALIZAR CITA - GUARDAR (PARA CITAS EXISTENTES)
    @PostMapping("/citas/actualizar/{id}")
    public String actualizarCita(@PathVariable Long id,
                               @RequestParam Long profesionalId,
                               @RequestParam Long servicioId,
                               @RequestParam String fecha,
                               @RequestParam String hora,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            System.out.println("💾 ACTUALIZANDO CITA: " + id);
            UsuarioEntity usuario = getUsuarioFromAuth(authentication);
            
            // ✅ OBTENER CITA EXISTENTE
            CitaEntity citaExistente = citaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            // Verificar que la cita pertenece al usuario
            if (!citaExistente.getUsuario().getId().equals(usuario.getId())) {
                throw new RuntimeException("No tienes permiso para editar esta cita");
            }
            
            // ✅ VERIFICAR NUEVOS OBJETOS
            ProfesionalEntity profesional = profesionalService.findById(profesionalId)
                    .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
            
            ServicioEntity servicio = servicioService.findById(servicioId)
                    .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
            
            // ✅ COMBINAR FECHA Y HORA
            LocalDateTime fechaHora = LocalDateTime.parse(fecha + "T" + hora);
            
            // ✅ ACTUALIZAR CITA EXISTENTE (NO crear nueva)
            citaExistente.setProfesional(profesional);
            citaExistente.setServicio(servicio);
            citaExistente.setFechaHora(fechaHora);
            // El estado se mantiene igual
            
            // ✅ GUARDAR CITA ACTUALIZADA
            CitaEntity citaActualizada = citaService.save(citaExistente);
            
            redirectAttributes.addFlashAttribute("success", 
                "Cita actualizada exitosamente para el " + fecha + " a las " + hora);
            return "redirect:/usuario/citas";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR ACTUALIZANDO CITA: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cita: " + e.getMessage());
            return "redirect:/usuario/citas/editar/" + id;
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
            CitaEntity citaActualizada = citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
            System.out.println("✅ CITA CANCELADA - ID: " + citaActualizada.getId());
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
            
            List<CitaEntity> citas = citaService.findByUsuarioIdManual(usuario.getId());
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
            UsuarioEntity usuarioActualizado = usuarioService.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
            System.out.println("✅ PERFIL ACTUALIZADO - ID: " + usuarioActualizado.getId());
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