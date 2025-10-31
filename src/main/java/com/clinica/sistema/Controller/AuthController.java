package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.Usuario;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        model.addAttribute("usuario", new Usuario());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Usuario usuario, Model model) {
        if (usuarioService.existsByEmail(usuario.getEmail())) {
            model.addAttribute("error", "El email ya está registrado");
            return "register";
        }
        
        usuario.setRol("USER");
        usuarioService.save(usuario);
        return "redirect:/login?success";
    }
}