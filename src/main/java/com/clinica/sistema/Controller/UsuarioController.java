package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user/perfil")  // ← RUTA BASE PARA GESTIÓN DE PERFIL
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 👤 VER PERFIL DEL USUARIO
    @GetMapping
    public String verPerfil(Authentication authentication, Model model) {
        String email = authentication.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("usuario", usuario);
        return "user/perfil/ver";
    }

    // ✏️ MOSTRAR FORMULARIO PARA EDITAR PERFIL
    @GetMapping("/editar")
    public String editarPerfilForm(Authentication authentication, Model model) {
        String email = authentication.getName();
        UsuarioEntity usuario = usuarioService.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        model.addAttribute("usuario", usuario);
        return "user/perfil/editar";
    }

    // 💾 ACTUALIZAR PERFIL
    @PostMapping("/actualizar")
    public String actualizarPerfil(@ModelAttribute UsuarioEntity usuarioActualizado,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            UsuarioEntity usuarioExistente = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Actualizar solo los campos permitidos
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setTelefono(usuarioActualizado.getTelefono());
            
            usuarioService.save(usuarioExistente);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
            return "redirect:/user/perfil";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/user/perfil/editar";
        }
    }

    // 🔐 CAMBIAR CONTRASEÑA (FORMULARIO)
    @GetMapping("/cambiar-password")
    public String cambiarPasswordForm() {
        return "user/perfil/cambiar-password";
    }

    // 🔐 ACTUALIZAR CONTRASEÑA
    @PostMapping("/actualizar-password")
    public String actualizarPassword(@RequestParam String passwordActual,
                                    @RequestParam String nuevaPassword,
                                    @RequestParam String confirmarPassword,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            UsuarioEntity usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
            // Validaciones
            if (!nuevaPassword.equals(confirmarPassword)) {
                redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden");
                return "redirect:/user/perfil/cambiar-password";
            }
            
            if (nuevaPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres");
                return "redirect:/user/perfil/cambiar-password";
            }
            
            // Actualizar contraseña
            usuario.setPassword(nuevaPassword);
            usuarioService.save(usuario);
            
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada exitosamente");
            return "redirect:/user/perfil";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar contraseña: " + e.getMessage());
            return "redirect:/user/perfil/cambiar-password";
        }
    }
}