package com.clinica.sistema.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    
    @GetMapping("/user/dashboard")
    public String userDashboard() {
        return "user/dashboard";
    }
    
    @GetMapping("/profesional/dashboard")
    public String profesionalDashboard() {
        return "profesional/dashboard";
    }
}