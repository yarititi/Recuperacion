package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("usuario", new UsuarioEntity());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UsuarioEntity usuario, Model model) {
        try {
            // Validar si el email ya existe
            if (usuarioService.existsByEmail(usuario.getEmail())) {
                model.addAttribute("error", "El email ya está registrado");
                model.addAttribute("usuario", usuario);
                return "register";
            }
            
            // Asignar rol por defecto
            usuario.setRol("USER");
            
            // Guardar usuario
            usuarioService.save(usuario);
            
            // Redirigir al login con mensaje de éxito
            return "redirect:/login?success";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar usuario: " + e.getMessage());
            model.addAttribute("usuario", usuario);
            return "register";
        }
    }

    
}