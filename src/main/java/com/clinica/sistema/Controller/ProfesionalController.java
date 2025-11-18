package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Service.CitaService;
import com.clinica.sistema.Service.ProfesionalService;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/profesional")
public class ProfesionalController {

    @Autowired
    private ProfesionalService profesionalService;

    @Autowired
    private CitaService citaService;

    @Autowired
    private UsuarioService usuarioService;

    // 🔧 MÉTODO AUXILIAR - Obtener profesional desde authentication
    private ProfesionalEntity getProfesionalFromAuth(Authentication authentication) {
        String email = authentication.getName();
        return profesionalService.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado"));
    }

    // 🏠 DASHBOARD DEL PROFESIONAL
    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 ACCEDIENDO AL DASHBOARD PROFESIONAL");
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            
            // Estadísticas básicas
            List<CitaEntity> citasProfesional = citaService.findByProfesionalId(profesional.getId());
            long citasHoy = citasProfesional.stream()
                    .filter(c -> c.getFechaHora().toLocalDate().equals(LocalDate.now()))
                    .count();
            
            long citasPendientes = citasProfesional.stream()
                    .filter(c -> "PENDIENTE".equals(c.getEstado()))
                    .count();
            
            long citasConfirmadas = citasProfesional.stream()
                    .filter(c -> "CONFIRMADA".equals(c.getEstado()))
                    .count();

            // Próximas citas
            List<CitaEntity> proximasCitas = citasProfesional.stream()
                    .filter(c -> c.getFechaHora().isAfter(LocalDateTime.now()))
                    .filter(c -> "PENDIENTE".equals(c.getEstado()) || "CONFIRMADA".equals(c.getEstado()))
                    .limit(5)
                    .collect(Collectors.toList());

            model.addAttribute("profesional", profesional);
            model.addAttribute("citasHoy", citasHoy);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            model.addAttribute("proximasCitas", proximasCitas);
            
            System.out.println("✅ DASHBOARD CARGADO - Citas hoy: " + citasHoy);
            return "profesional/dashboard";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN DASHBOARD: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "profesional/dashboard";
        }
    }

    // 📅 CITAS - LISTAR CON FILTROS
    @GetMapping("/citas")
    public String listarCitas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta,
            Authentication authentication, 
            Model model) {
        
        try {
            System.out.println("🎯 ACCEDIENDO A LISTA DE CITAS");
            System.out.println("🔍 FILTROS - Estado: " + estado + ", FechaDesde: " + fechaDesde + ", FechaHasta: " + fechaHasta);
            
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            List<CitaEntity> todasLasCitas = citaService.findByProfesionalId(profesional.getId());

            // ✅ FILTRADO SEGURO - Si no hay filtros, mostrar todas las citas
            List<CitaEntity> citasFiltradas = todasLasCitas;
            
            if (estado != null && !estado.isEmpty()) {
                citasFiltradas = citasFiltradas.stream()
                        .filter(cita -> estado.equals(cita.getEstado()))
                        .collect(Collectors.toList());
                System.out.println("✅ FILTRADO POR ESTADO: " + estado);
            }
            
            // Filtro por fecha desde
            if (fechaDesde != null && !fechaDesde.isEmpty()) {
                LocalDate desde = LocalDate.parse(fechaDesde);
                citasFiltradas = citasFiltradas.stream()
                        .filter(cita -> !cita.getFechaHora().toLocalDate().isBefore(desde))
                        .collect(Collectors.toList());
                System.out.println("✅ FILTRADO POR FECHA DESDE: " + fechaDesde);
            }
            
            // Filtro por fecha hasta
            if (fechaHasta != null && !fechaHasta.isEmpty()) {
                LocalDate hasta = LocalDate.parse(fechaHasta);
                citasFiltradas = citasFiltradas.stream()
                        .filter(cita -> !cita.getFechaHora().toLocalDate().isAfter(hasta))
                        .collect(Collectors.toList());
                System.out.println("✅ FILTRADO POR FECHA HASTA: " + fechaHasta);
            }

            // Estadísticas (siempre basadas en TODAS las citas)
            long citasHoy = todasLasCitas.stream()
                    .filter(c -> c.getFechaHora().toLocalDate().equals(LocalDate.now()))
                    .count();
            long citasPendientes = todasLasCitas.stream()
                    .filter(c -> "PENDIENTE".equals(c.getEstado()))
                    .count();
            long citasConfirmadas = todasLasCitas.stream()
                    .filter(c -> "CONFIRMADA".equals(c.getEstado()))
                    .count();

            model.addAttribute("citas", citasFiltradas);
            model.addAttribute("profesional", profesional);
            model.addAttribute("totalCitas", todasLasCitas.size());
            model.addAttribute("citasHoy", citasHoy);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            
            // Pasar los parámetros de filtro para mantenerlos en el template
            model.addAttribute("filtroEstado", estado);
            model.addAttribute("filtroFechaDesde", fechaDesde);
            model.addAttribute("filtroFechaHasta", fechaHasta);
            
            System.out.println("✅ CITAS CARGADAS: " + citasFiltradas.size() + " citas después de filtros");
            return "profesional/citas/listar";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN LISTAR CITAS: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            return "profesional/citas/listar";
        }
    }

    // 👥 PACIENTES - LISTAR
    @GetMapping("/pacientes")
    public String listarPacientes(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 ACCEDIENDO A LISTA DE PACIENTES");
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            List<CitaEntity> citas = citaService.findByProfesionalId(profesional.getId());
            
            // Obtener pacientes únicos
            List<UsuarioEntity> pacientes = citas.stream()
                    .map(CitaEntity::getUsuario)
                    .distinct()
                    .collect(Collectors.toList());

            model.addAttribute("pacientes", pacientes);
            model.addAttribute("profesional", profesional);
            System.out.println("✅ PACIENTES CARGADOS: " + pacientes.size() + " pacientes únicos");
            return "profesional/pacientes/listar";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN LISTAR PACIENTES: " + e.getMessage());
            model.addAttribute("error", "Error al cargar los pacientes: " + e.getMessage());
            return "profesional/pacientes/listar";
        }
    }

    // 👤 HISTORIAL DE PACIENTE ESPECÍFICO - NUEVO MÉTODO
    @GetMapping("/pacientes/historial/{pacienteId}")
    public String verHistorialPaciente(@PathVariable Long pacienteId, 
                                     Authentication authentication, 
                                     Model model) {
        try {
            System.out.println("🎯 VIENDO HISTORIAL DEL PACIENTE ID: " + pacienteId);
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            
            // Verificar que el paciente existe
            UsuarioEntity paciente = usuarioService.findById(pacienteId)
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            
            // Obtener citas del paciente con este profesional
            List<CitaEntity> citasPaciente = citaService.findByProfesionalId(profesional.getId())
                    .stream()
                    .filter(cita -> cita.getUsuario().getId().equals(pacienteId))
                    .collect(Collectors.toList());

            model.addAttribute("paciente", paciente);
            model.addAttribute("profesional", profesional);
            model.addAttribute("citas", citasPaciente);
            model.addAttribute("totalCitas", citasPaciente.size());
            
            System.out.println("✅ HISTORIAL CARGADO: " + citasPaciente.size() + " citas del paciente");
            return "profesional/pacientes/historial";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN HISTORIAL PACIENTE: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el historial: " + e.getMessage());
            return "redirect:/profesional/pacientes";
        }
    }

    // 🕐 HORARIOS - GESTIONAR (VISTA PRINCIPAL) - CORREGIDO
    @GetMapping("/horarios")
    public String gestionarHorarios(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 ACCEDIENDO A GESTIÓN DE HORARIOS");
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            
            model.addAttribute("profesional", profesional);
            System.out.println("✅ HORARIOS CARGADOS - Profesional: " + profesional.getUsuario().getNombre());
            return "profesional/horarios"; // ← CORREGIDO: Ahora apunta a horarios.html
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN GESTIÓN DE HORARIOS: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al cargar los horarios: " + e.getMessage());
            return "profesional/horarios"; // ← CORREGIDO
        }
    }

    // 💾 ACTUALIZAR HORARIOS - NUEVO MÉTODO PARA TU FORMULARIO
    @PostMapping("/horarios/actualizar")
    public String actualizarHorario(@RequestParam(required = false) String horarioDisponible,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            System.out.println("💾 ACTUALIZANDO HORARIO: " + horarioDisponible);
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            
            if (horarioDisponible != null && !horarioDisponible.trim().isEmpty()) {
                LocalDateTime horarioDateTime = LocalDateTime.parse(horarioDisponible);
                profesional.setHorarioDisponible(horarioDateTime);
                System.out.println("✅ HORARIO ESTABLECIDO: " + horarioDateTime);
                redirectAttributes.addFlashAttribute("success", "Horario actualizado exitosamente");
            } else {
                profesional.setHorarioDisponible(null);
                System.out.println("✅ HORARIO LIMPIADO");
                redirectAttributes.addFlashAttribute("success", "Horario limpiado exitosamente");
            }
            
            profesionalService.save(profesional);
            System.out.println("✅ HORARIO GUARDADO EXITOSAMENTE");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR ACTUALIZANDO HORARIO: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar horario: " + e.getMessage());
        }
        
        return "redirect:/profesional/horarios";
    }

    // ⚙️ PERFIL - VER (ACTUALIZADO CON ESTADÍSTICAS)
    @GetMapping("/perfil")
    public String verPerfil(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 ACCEDIENDO A PERFIL");
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            
            // ✅ OBTENER ESTADÍSTICAS PARA MOSTRAR EN EL PERFIL
            List<CitaEntity> citasProfesional = citaService.findByProfesionalId(profesional.getId());
            long citasHoy = citasProfesional.stream()
                    .filter(c -> c.getFechaHora().toLocalDate().equals(LocalDate.now()))
                    .count();
            long citasPendientes = citasProfesional.stream()
                    .filter(c -> "PENDIENTE".equals(c.getEstado()))
                    .count();
            long citasConfirmadas = citasProfesional.stream()
                    .filter(c -> "CONFIRMADA".equals(c.getEstado()))
                    .count();

            model.addAttribute("profesional", profesional);
            model.addAttribute("citasHoy", citasHoy);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            
            System.out.println("✅ PERFIL CARGADO - Citas hoy: " + citasHoy);
            return "profesional/perfil/ver";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN PERFIL: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el perfil: " + e.getMessage());
            return "redirect:/profesional/dashboard";
        }
    }

    // ✏️ PERFIL - EDITAR (FORMULARIO)
    @GetMapping("/perfil/editar")
    public String editarPerfilForm(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 ACCEDIENDO A EDITAR PERFIL");
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            model.addAttribute("profesional", profesional);
            return "profesional/perfil/editar";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN EDITAR PERFIL: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/profesional/perfil";
        }
    }

    // 💾 PERFIL - ACTUALIZAR
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@RequestParam String nombre,
                                  @RequestParam String telefono,
                                  @RequestParam String especialidad,
                                  @RequestParam(required = false) String descripcion,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            System.out.println("💾 ACTUALIZANDO PERFIL: " + nombre);
            ProfesionalEntity profesionalExistente = getProfesionalFromAuth(authentication);
            
            // Actualizar datos del usuario
            profesionalExistente.getUsuario().setNombre(nombre);
            profesionalExistente.getUsuario().setTelefono(telefono);
            
            // Actualizar datos profesionales
            profesionalExistente.setEspecialidad(especialidad);
            if (descripcion != null) {
                profesionalExistente.setDescripcion(descripcion);
            }
            
            profesionalService.save(profesionalExistente);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
            System.out.println("✅ PERFIL ACTUALIZADO EXITOSAMENTE");
            return "redirect:/profesional/perfil";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR ACTUALIZANDO PERFIL: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/profesional/perfil/editar";
        }
    }

    // 🔐 CAMBIAR CONTRASEÑA (FORMULARIO)
    @GetMapping("/perfil/cambiar-password")
    public String cambiarPasswordForm(Authentication authentication, Model model) {
        try {
            System.out.println("🎯 ACCEDIENDO A CAMBIAR CONTRASEÑA");
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            model.addAttribute("profesional", profesional);
            return "profesional/perfil/cambiar-password";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR EN CAMBIAR CONTRASEÑA: " + e.getMessage());
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/profesional/perfil";
        }
    }

    // 🔐 PROCESAR CAMBIO DE CONTRASEÑA
    @PostMapping("/perfil/cambiar-password")
    public String cambiarPassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            System.out.println("🔐 CAMBIANDO CONTRASEÑA");
            String email = authentication.getName();
            usuarioService.cambiarPassword(email, currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada exitosamente");
            System.out.println("✅ CONTRASEÑA CAMBIADA EXITOSAMENTE");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR CAMBIANDO CONTRASEÑA: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/profesional/perfil";
    }

    // 👀 VER DETALLE DE CITA
    @GetMapping("/citas/{id}")
    public String verCita(@PathVariable Long id, Authentication authentication, Model model) {
        try {
            System.out.println("🎯 VIENDO DETALLE DE CITA ID: " + id);
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            CitaEntity cita = citaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            if (!cita.getProfesional().getId().equals(profesional.getId())) {
                throw new RuntimeException("No tienes permiso para ver esta cita");
            }

            // ✅ DEBUG - Verificar datos
            System.out.println("✅ CITA ENCONTRADA:");
            System.out.println("   - ID: " + cita.getId());
            System.out.println("   - Paciente: " + cita.getUsuario().getNombre());
            System.out.println("   - Servicio: " + (cita.getServicio() != null ? cita.getServicio().getNombre() : "NULL"));
            System.out.println("   - Estado: " + cita.getEstado());

            model.addAttribute("cita", cita);
            model.addAttribute("profesional", profesional);
            System.out.println("✅ DETALLE DE CITA CARGADO: " + cita.getEstado());
            return "profesional/citas/detalle";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR VIENDO DETALLE DE CITA: " + e.getMessage());
            model.addAttribute("error", "Error al cargar la cita: " + e.getMessage());
            return "redirect:/profesional/citas";
        }
    }

    // ✅ CONFIRMAR CITA
    @PostMapping("/citas/{id}/confirmar")
    public String confirmarCita(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("✅ CONFIRMANDO CITA ID: " + id);
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            CitaEntity cita = citaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            if (!cita.getProfesional().getId().equals(profesional.getId())) {
                throw new RuntimeException("No tienes permiso para confirmar esta cita");
            }

            cita.setEstado("CONFIRMADA");
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita confirmada exitosamente");
            System.out.println("✅ CITA CONFIRMADA EXITOSAMENTE");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR CONFIRMANDO CITA: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al confirmar cita: " + e.getMessage());
        }
        
        return "redirect:/profesional/citas";
    }

    // ❌ CANCELAR CITA
    @PostMapping("/citas/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("❌ CANCELANDO CITA ID: " + id);
            ProfesionalEntity profesional = getProfesionalFromAuth(authentication);
            CitaEntity cita = citaService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            if (!cita.getProfesional().getId().equals(profesional.getId())) {
                throw new RuntimeException("No tienes permiso para cancelar esta cita");
            }

            cita.setEstado("CANCELADA");
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
            System.out.println("✅ CITA CANCELADA EXITOSAMENTE");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR CANCELANDO CITA: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
        }
        
        return "redirect:/profesional/citas";
    }

    // 🔄 MÉTODO PARA PRUEBAS - Redirección segura
    @GetMapping("/")
    public String redireccionarDashboard() {
        System.out.println("🔄 REDIRIGIENDO AL DASHBOARD");
        return "redirect:/profesional/dashboard";
    }
}