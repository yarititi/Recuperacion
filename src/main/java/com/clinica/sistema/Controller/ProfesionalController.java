package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Service.CitaService;
import com.clinica.sistema.Service.ProfesionalService;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/profesional")
public class ProfesionalController {

    @Autowired
    private ProfesionalService profesionalService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private CitaService citaService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        ProfesionalEntity profesional = profesionalService.findByUsuarioEmail(email).orElseThrow();
        List<CitaEntity> citas = citaService.findByProfesionalId(profesional.getId());
        
        model.addAttribute("profesional", profesional);
        model.addAttribute("citas", citas);
        return "profesional/dashboard";
    }

    @PostMapping("/citas/{id}/estado")
    public String actualizarEstadoCita(@PathVariable Long id, @RequestParam String estado) {
        CitaEntity cita = citaService.findById(id).orElseThrow();
        cita.setEstado(estado);
        citaService.save(cita);
        return "redirect:/profesional/dashboard?updated";
    }
}