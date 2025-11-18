package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Service.UsuarioService;
import com.clinica.sistema.Service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String registerForm(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UsuarioEntity usuario, 
                          @RequestParam(required = false) String tipoUsuario,
                          Model model) {
        try {
            if (usuarioService.existsByEmail(usuario.getEmail())) {
                model.addAttribute("error", "El email ya está registrado");
                model.addAttribute("usuario", usuario);
                return "auth/register";
            }
            
            if ("profesional".equals(tipoUsuario)) {
                usuario.setRol("PROFESIONAL");
            } else {
                usuario.setRol("USER");
            }
            
            usuarioService.save(usuario);
            
            if ("PROFESIONAL".equals(usuario.getRol())) {
                ProfesionalEntity profesional = new ProfesionalEntity();
                profesional.setUsuario(usuario);
                profesional.setEspecialidad("Medicina General");
                profesionalService.save(profesional);
            }
            
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