package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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
            
            // Estadísticas
            long totalUsuarios = usuarioService.countAllUsers();
            long totalProfesionales = usuarioService.countByRol("PROFESIONAL");
            long totalPacientes = usuarioService.countByRol("USER");
            
            // Usuarios recientes
            List<UsuarioEntity> usuariosRecientes = usuarioService.findRecentUsers();

            model.addAttribute("admin", admin);
            model.addAttribute("totalUsuarios", totalUsuarios);
            model.addAttribute("totalProfesionales", totalProfesionales);
            model.addAttribute("totalPacientes", totalPacientes);
            model.addAttribute("usuariosRecientes", usuariosRecientes);
            
            return "admin/dashboard";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el dashboard: " + e.getMessage());
            return "admin/dashboard";
        }
    }

    @GetMapping("/usuarios")
    public String gestionarUsuarios(Model model) {
        try {
            List<UsuarioEntity> usuarios = usuarioService.findAll();
            model.addAttribute("usuarios", usuarios);
            return "admin/usuarios/listar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            return "admin/usuarios/listar";
        }
    }

    @GetMapping("/profesionales")
    public String gestionarProfesionales(Model model) {
        try {
            List<UsuarioEntity> profesionales = usuarioService.findByRol("PROFESIONAL");
            model.addAttribute("profesionales", profesionales);
            return "admin/profesionales/listar";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar profesionales: " + e.getMessage());
            return "admin/profesionales/listar";
        }
    }
}