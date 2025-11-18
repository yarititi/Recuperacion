package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Service.UsuarioService;
import com.clinica.sistema.Service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfesionalService profesionalService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "success", required = false) String success,
                       Model model) {
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas");
        }
        if (logout != null) {
            model.addAttribute("message", "Sesión cerrada exitosamente");
        }
        if (success != null) {
            model.addAttribute("message", "Registro exitoso. Por favor inicia sesión.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model, HttpServletRequest request) {
        model.addAttribute("usuario", new UsuarioEntity());
        
        // Add CSRF token to the model
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }
        
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UsuarioEntity usuario, 
                          @RequestParam(required = false) String tipoUsuario,
                          @RequestParam(required = false) String telefono,
                          Model model, HttpServletRequest request) {
        try {
            // Validar campos requeridos
            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty() ||
                usuario.getEmail() == null || usuario.getEmail().trim().isEmpty() ||
                usuario.getPassword() == null || usuario.getPassword().trim().isEmpty()) {
                model.addAttribute("error", "Por favor complete todos los campos obligatorios");
                model.addAttribute("usuario", usuario);
                return "auth/register";
            }

            // Verificar si el correo ya está registrado
            if (usuarioService.existsByEmail(usuario.getEmail())) {
                model.addAttribute("error", "El email ya está registrado");
                model.addAttribute("usuario", usuario);
                return "auth/register";
            }
            
            // Establecer el rol del usuario
            if ("profesional".equals(tipoUsuario)) {
                usuario.setRol("PROFESIONAL");
            } else {
                usuario.setRol("USER");
            }
            
            // Establecer fecha de registro y creación
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario.setFechaCreacion(LocalDateTime.now());
            
            // Establecer teléfono (si se proporcionó)
            if (telefono != null && !telefono.trim().isEmpty()) {
                usuario.setTelefono(telefono.trim());
            } else {
                // Si el teléfono es obligatorio, descomenta la siguiente línea
                // throw new IllegalArgumentException("El teléfono es obligatorio");
                usuario.setTelefono("0000000000"); // Valor por defecto temporal
            }
            
            // Establecer usuario como activo
            usuario.setActivo(true);
            
            // Guardar el usuario
            UsuarioEntity usuarioGuardado = usuarioService.save(usuario);
            
            // Si es profesional, crear el perfil de profesional
            if ("PROFESIONAL".equals(usuario.getRol())) {
                ProfesionalEntity profesional = new ProfesionalEntity();
                profesional.setUsuario(usuarioGuardado);
                profesional.setEspecialidad("Medicina General"); // Valor por defecto
                profesionalService.save(profesional);
            }
            
            // Redirigir al login con mensaje de éxito
            return "redirect:/login?success";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            return "auth/register";
        }
    }

    @GetMapping("/auth/success")
    public String loginSuccess(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login?error=true";
        }
        
        String email = authentication.getName();
        System.out.println("🔑 LOGIN EXITOSO - Redirigiendo: " + email);
        
        try {
            UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            if ("PROFESIONAL".equals(usuario.getRol())) {
                return "redirect:/profesional/dashboard";
            } else if ("USER".equals(usuario.getRol())) {
                return "redirect:/usuario/dashboard";  // ✅ CORREGIDO: /usuario/dashboard
            } else if ("ADMIN".equals(usuario.getRol())) {
                return "redirect:/admin/dashboard";
            }
            
        } catch (Exception e) {
            System.err.println("Error en redirección: " + e.getMessage());
            e.printStackTrace();  // ✅ Agregar para debug
        }
        
        return "redirect:/";
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "auth/access-denied";
    }
}