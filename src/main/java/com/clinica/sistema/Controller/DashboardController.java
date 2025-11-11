package com.clinica.sistema.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Collection;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String rol = obtenerRolUsuario(authentication);
            model.addAttribute("rolUsuario", rol);
        }
        return "dashboard";
    }

    private String obtenerRolUsuario(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        if (authorities != null && !authorities.isEmpty()) {
            String authority = authorities.iterator().next().getAuthority();
            return authority.startsWith("ROLE_") ? authority.substring(5) : authority;
        }
        
        return "USER";
    }
}