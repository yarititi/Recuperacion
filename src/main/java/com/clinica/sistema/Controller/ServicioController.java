package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @GetMapping("")
    public String listarServicios(Model model) {
        model.addAttribute("servicios", servicioService.findByActivo(true));
        return "admin/servicios/listar";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("servicio", new ServicioEntity());
        return "admin/servicios/formulario";
    }

    @PostMapping("/guardar")
    public String guardarServicio(@ModelAttribute ServicioEntity servicio, RedirectAttributes redirectAttributes) {
        try {
            servicio.setActivo(true);
            servicioService.save(servicio);
            redirectAttributes.addFlashAttribute("success", "Servicio guardado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar el servicio: " + e.getMessage());
        }
        return "redirect:/admin/servicios";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        servicioService.findById(id).ifPresent(servicio -> model.addAttribute("servicio", servicio));
        return "admin/servicios/formulario";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarServicio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            servicioService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Servicio eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el servicio: " + e.getMessage());
        }
        return "redirect:/admin/servicios";
    }
}
