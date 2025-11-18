package com.clinica.sistema.Controller;

import com.clinica.sistema.Entity.CitaEntity;
import com.clinica.sistema.Entity.ServicioEntity;
import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Entity.ProfesionalEntity;
import com.clinica.sistema.Service.CitaService;
import com.clinica.sistema.Service.ServicioService;
import com.clinica.sistema.Service.UsuarioService;
import com.clinica.sistema.Service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/api/citas")
public class CitaApiController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfesionalService profesionalService;

    // ✔ LISTAR CITAS DEL USUARIO
    @GetMapping("/listar")
    public Map<String, Object> listarCitas(Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                response.put("error", "Usuario no encontrado");
                return response;
            }

            UsuarioEntity usuario = usuarioOpt.get();
            List<CitaEntity> citas = citaService.findByUsuarioId(usuario.getId());

            response.put("citas", citas);
            response.put("usuario", usuario);

            return response;

        } catch (Exception e) {
            response.put("error", "Error al cargar citas: " + e.getMessage());
            return response;
        }
    }


    // ✔ VER DETALLE DE UNA CITA
    @GetMapping("/detalle/{id}")
    public Map<String, Object> verDetalle(@PathVariable Long id,
                                          Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                response.put("error", "Usuario no encontrado");
                return response;
            }

            UsuarioEntity usuario = usuarioOpt.get();
            Optional<CitaEntity> citaOpt = citaService.findById(id);

            if (citaOpt.isEmpty()) {
                response.put("error", "Cita no encontrada");
                return response;
            }

            CitaEntity cita = citaOpt.get();

            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                response.put("error", "No tienes permiso para ver esta cita");
                return response;
            }

            response.put("cita", cita);
            return response;

        } catch (Exception e) {
            response.put("error", "Error al cargar detalles: " + e.getMessage());
            return response;
        }
    }


    // ✔ CREAR O EDITAR (misma función que antes)
    @PostMapping("/guardar")
    public Map<String, Object> guardarCita(@RequestBody Map<String, String> data,
                                           Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                response.put("error", "Usuario no encontrado");
                return response;
            }

            UsuarioEntity usuario = usuarioOpt.get();

            Long id = data.containsKey("id") ? Long.valueOf(data.get("id")) : null;
            Long servicioId = Long.valueOf(data.get("servicioId"));
            Long profesionalId = Long.valueOf(data.get("profesionalId"));
            String fecha = data.get("fecha");
            String hora = data.get("hora");

            Optional<ServicioEntity> servicioOpt = servicioService.findById(servicioId);
            Optional<ProfesionalEntity> profesionalOpt = profesionalService.findById(profesionalId);

            if (servicioOpt.isEmpty() || profesionalOpt.isEmpty()) {
                response.put("error", "Servicio o Profesional no encontrado");
                return response;
            }

            ServicioEntity servicio = servicioOpt.get();
            ProfesionalEntity profesional = profesionalOpt.get();

            LocalDate fechaDate = LocalDate.parse(fecha);
            LocalTime horaTime = LocalTime.parse(hora);
            LocalDateTime fechaHora = LocalDateTime.of(fechaDate, horaTime);

            CitaEntity cita;

            if (id != null) {
                // EDITAR CITA
                Optional<CitaEntity> citaOpt = citaService.findById(id);

                if (citaOpt.isEmpty()) {
                    response.put("error", "Cita no encontrada");
                    return response;
                }

                cita = citaOpt.get();

                if (!cita.getUsuario().getId().equals(usuario.getId())) {
                    response.put("error", "No tienes permiso para editar esta cita");
                    return response;
                }

                cita.setFechaHora(fechaHora);
                cita.setServicio(servicio);
                cita.setProfesional(profesional);

                citaService.save(cita);

                response.put("success", "Cita actualizada exitosamente");
                response.put("cita", cita);

            } else {
                // CREAR NUEVA
                cita = new CitaEntity();
                cita.setFechaHora(fechaHora);
                cita.setUsuario(usuario);
                cita.setServicio(servicio);
                cita.setProfesional(profesional);

                citaService.save(cita);

                response.put("success", "Cita creada exitosamente");
                response.put("cita", cita);
            }

            return response;

        } catch (Exception e) {
            response.put("error", "Error al guardar la cita: " + e.getMessage());
            return response;
        }
    }


    // ✔ CANCELAR CITA
    @PostMapping("/cancelar/{id}")
    public Map<String, Object> cancelarCita(@PathVariable Long id,
                                            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                response.put("error", "Usuario no encontrado");
                return response;
            }

            UsuarioEntity usuario = usuarioOpt.get();
            Optional<CitaEntity> citaOpt = citaService.findById(id);

            if (citaOpt.isEmpty()) {
                response.put("error", "Cita no encontrada");
                return response;
            }

            CitaEntity cita = citaOpt.get();

            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                response.put("error", "No tienes permiso");
                return response;
            }

            if (!cita.getEstado().equals("PENDIENTE") && !cita.getEstado().equals("CONFIRMADA")) {
                response.put("error", "No se puede cancelar una cita " + cita.getEstado());
                return response;
            }

            cita.setEstado("CANCELADA");
            citaService.save(cita);

            response.put("success", "Cita cancelada");

            return response;

        } catch (Exception e) {
            response.put("error", "Error al cancelar cita: " + e.getMessage());
            return response;
        }
    }


    // ✔ ELIMINAR CITA
    @DeleteMapping("/eliminar/{id}")
    public Map<String, Object> eliminarCita(@PathVariable Long id,
                                            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        try {
            String email = authentication.getName();
            Optional<UsuarioEntity> usuarioOpt = usuarioService.findByEmail(email);

            if (usuarioOpt.isEmpty()) {
                response.put("error", "Usuario no encontrado");
                return response;
            }

            UsuarioEntity usuario = usuarioOpt.get();

            Optional<CitaEntity> citaOpt = citaService.findById(id);

            if (citaOpt.isEmpty()) {
                response.put("error", "Cita no encontrada");
                return response;
            }

            CitaEntity cita = citaOpt.get();

            if (!cita.getUsuario().getId().equals(usuario.getId())) {
                response.put("error", "No tienes permiso");
                return response;
            }

            if (!"CANCELADA".equals(cita.getEstado())) {
                response.put("error", "Solo se pueden eliminar citas canceladas");
                return response;
            }

            citaService.deleteById(id);

            response.put("success", "Cita eliminada");
            return response;

        } catch (Exception e) {
            response.put("error", "Error al eliminar cita: " + e.getMessage());
            return response;
        }
    }
}
