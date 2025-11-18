package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        try {
            String email = authentication.getName();
            UsuarioEntity admin = usuarioService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
            
            // Ensure admin has a name
            if (admin.getNombre() == null || admin.getNombre().isBlank()) {
                admin.setNombre("Administrador");
            }
            
            // Estadísticas
            long totalUsuarios = usuarioService.countAllUsers();
            long totalProfesionales = usuarioService.countByRol("PROFESIONAL");
            long totalPacientes = usuarioService.countByRol("USER");
            
            // Usuarios recientes - Asegurarse de que nunca sea null
            List<UsuarioEntity> usuariosRecientes = usuarioService.findRecentUsers();
            if (usuariosRecientes == null) {
                usuariosRecientes = new ArrayList<>();
            }

            model.addAttribute("admin", admin);
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("totalProfesionales", totalProfesionales);
            model.addAttribute("totalPacientes", totalPacientes);
            model.addAttribute("usuariosRecientes", usuariosRecientes);
            
            return "admin/dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            // Create a default admin object to prevent template errors
            UsuarioEntity defaultAdmin = new UsuarioEntity();
            defaultAdmin.setNombre("Administrador");
            model.addAttribute("admin", defaultAdmin);
            return "admin/dashboard";
        }
    }

    @GetMapping("/usuarios")
    public String gestionarUsuarios(Model model, HttpServletRequest request) {
        try {
            List<UsuarioEntity> usuarios = usuarioService.findAll();
            model.addAttribute("usuarios", usuarios);
            
            // Add CSRF token to the model
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("_csrf", csrfToken);
            }
            
            return "admin/usuarios/listar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            return "admin/usuarios/listar";
        }
    }

    @GetMapping("/profesionales")
    public String gestionarProfesionales(Model model, HttpServletRequest request) {
        try {
            List<UsuarioEntity> profesionales = usuarioService.findByRol("PROFESIONAL");
            model.addAttribute("profesionales", profesionales);
            
            // Add CSRF token to the model
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken == null) {
                csrfToken = new CsrfToken() {
                    @Override
                    public String getHeaderName() {
                        return "_csrf";
                    }
                    @Override
                    public String getParameterName() {
                        return "_csrf";
                    }
                    @Override
                    public String getToken() {
                        return "";
                    }
                };
            }
            model.addAttribute("_csrf", csrfToken);
            
            return "admin/profesionales/listar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar profesionales: " + e.getMessage());
            return "admin/profesionales/listar";
        }
    }

    @GetMapping("/usuarios/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            UsuarioEntity usuario = usuarioService.findById(id)
                .orElseGet(() -> {
                    UsuarioEntity newUser = new UsuarioEntity();
                    newUser.setActivo(true);
                    return newUser;
                });
            
            // Ensure required fields are initialized
            if (usuario.getActivo() == null) {
                usuario.setActivo(true);
            }
            
            // Add CSRF token to the model
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("_csrf", csrfToken);
            } else {
                // If CSRF token is null, create a mock one to prevent template errors
                model.addAttribute("_csrf", new CsrfToken() {
                    @Override
                    public String getHeaderName() {
                        return "_csrf";
                    }
                    @Override
                    public String getParameterName() {
                        return "_csrf";
                    }
                    @Override
                    public String getToken() {
                        return "";
                    }
                });
            }
            
            model.addAttribute("usuario", usuario);
            return "admin/usuarios/formulario";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(
            @RequestParam(required = false) Long id,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam String rol,
            @RequestParam(required = false) String nuevaContrasena,
            @RequestParam(defaultValue = "true") boolean activo,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Get the existing user or create a new one
            UsuarioEntity usuario = id != null ? 
                usuarioService.findById(id).orElse(new UsuarioEntity()) : 
                new UsuarioEntity();
            
            // Update user fields
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setRol(rol);
            usuario.setActivo(activo);
            
            // Only update password if a new one is provided
            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                usuario.setPassword(nuevaContrasena);
            }
            
            // Save the user
            usuarioService.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Usuario guardado exitosamente");
            return "redirect:/admin/usuarios";
            
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar el usuario: " + e.getMessage());
            
            // If we have an ID, redirect back to the edit page, otherwise to the list
            return id != null ? 
                "redirect:/admin/usuarios/editar/" + id : 
                "redirect:/admin/usuarios";
        }
    }

    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
    
    @GetMapping("/profesionales/editar/{id}")
    public String mostrarFormularioEdicionProfesional(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            UsuarioEntity profesional = usuarioService.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado con ID: " + id));
            
            // Ensure required fields are initialized
            if (profesional.getActivo() == null) {
                profesional.setActivo(true);
            }
            
            // Add CSRF token to the model
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                model.addAttribute("_csrf", csrfToken);
            } else {
                // If CSRF token is null, create a mock one to prevent template errors
                model.addAttribute("_csrf", new CsrfToken() {
                    @Override public String getHeaderName() { return "_csrf"; }
                    @Override public String getParameterName() { return "_csrf"; }
                    @Override public String getToken() { return ""; }
                });
            }
            
            model.addAttribute("profesional", profesional);
            return "admin/profesionales/formulario";
            
        } catch (Exception e) {
            return "redirect:/admin/profesionales?error=Error al cargar el profesional: " + e.getMessage();
        }
    }
    
    @PostMapping("/profesionales/eliminar/{id}")
    public String eliminarProfesional(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Profesional eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el profesional: " + e.getMessage());
        }
        return "redirect:/admin/profesionales";
    }
    
    @PostMapping("/profesionales/guardar")
    public String guardarProfesional(
            @RequestParam(required = false) Long id,
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) String documento,
            @RequestParam(required = false) String nuevaContrasena,
            @RequestParam(defaultValue = "true") boolean activo,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Get the existing user or create a new one
            UsuarioEntity usuario = id != null ? 
                usuarioService.findById(id).orElse(new UsuarioEntity()) : 
                new UsuarioEntity();
            
            // Update user fields
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setTelefono(telefono);
            usuario.setRol("PROFESIONAL");
            usuario.setActivo(activo);
            
            // Only update password if a new one is provided
            if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                usuario.setPassword(nuevaContrasena);
            }
            
            // Save the user first to get an ID if it's a new user
            usuario = usuarioService.save(usuario);
            
            // Handle ProfesionalEntity
            ProfesionalEntity profesional = usuario.getProfesional();
            if (profesional == null) {
                profesional = new ProfesionalEntity();
                profesional.setUsuario(usuario);
            }
            
            // Update professional-specific fields
            profesional.setEspecialidad(especialidad);
           
            
            // Set the professional back to the user and save again
            usuario.setProfesional(profesional);
            usuarioService.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Profesional guardado exitosamente");
            return "redirect:/admin/profesionales";
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar el profesional: " + e.getMessage());
            
            // If we have an ID, redirect back to the edit page, otherwise to the list
            return id != null ? 
                "redirect:/admin/profesionales/editar/" + id : 
                "redirect:/admin/profesionales";
        }
    }
}