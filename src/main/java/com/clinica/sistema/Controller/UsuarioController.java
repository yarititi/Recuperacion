package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.Cita;
import com.clinica.sistema.Entity.Servicio;
import com.clinica.sistema.Entity.Usuario;
import com.clinica.sistema.Service.CitaService;
import com.clinica.sistema.Service.ServicioService;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private CitaService citaService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElseThrow();
        List<Cita> citas = citaService.findByUsuarioId(usuario.getId());
        
        model.addAttribute("usuario", usuario);
        model.addAttribute("citas", citas);
        return "user/dashboard";
    }

    @GetMapping("/citas/nueva")
    public String nuevaCitaForm(Model model) {
        List<Servicio> servicios = servicioService.findAll();
        model.addAttribute("cita", new Cita());
        model.addAttribute("servicios", servicios);
        return "user/nueva-cita";
    }

    @PostMapping("/citas/nueva")
    public String crearCita(@ModelAttribute Cita cita, Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(email).orElseThrow();
        cita.setUsuario(usuario);
        citaService.save(cita);
        return "redirect:/user/dashboard?success";
    }
}