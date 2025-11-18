package com.clinica.sistema.Config;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializerConfig implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuario admin si no existe
        if (usuarioRepository.findByEmail("admin@clinica.com").isEmpty()) {
            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Administrador");
            admin.setEmail("admin@clinica.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol("ADMIN");
            admin.setActivo(true);
            admin.setFechaRegistro(LocalDateTime.now());
            admin.setFechaCreacion(LocalDateTime.now());
            
            usuarioRepository.save(admin);
            System.out.println("✅ Usuario ADMIN creado: admin@clinica.com / admin123");
        }

        // Crear usuario profesional de prueba
        if (usuarioRepository.findByEmail("doctor@clinica.com").isEmpty()) {
            UsuarioEntity doctor = new UsuarioEntity();
            doctor.setNombre("Dr. Juan Pérez");
            doctor.setEmail("doctor@clinica.com");
            doctor.setPassword(passwordEncoder.encode("doctor123"));
            doctor.setRol("PROFESIONAL");
            doctor.setActivo(true);
            doctor.setFechaRegistro(LocalDateTime.now());
            doctor.setFechaCreacion(LocalDateTime.now());
            
            usuarioRepository.save(doctor);
            System.out.println("✅ Usuario PROFESIONAL creado: doctor@clinica.com / doctor123");
        }

        // Crear usuario paciente de prueba
        if (usuarioRepository.findByEmail("paciente@clinica.com").isEmpty()) {
            UsuarioEntity paciente = new UsuarioEntity();
            paciente.setNombre("María García");
            paciente.setEmail("paciente@clinica.com");
            paciente.setPassword(passwordEncoder.encode("paciente123"));
            paciente.setRol("USER");
            paciente.setActivo(true);
            paciente.setFechaRegistro(LocalDateTime.now());
            paciente.setFechaCreacion(LocalDateTime.now());
            
            usuarioRepository.save(paciente);
            System.out.println("✅ Usuario PACIENTE creado: paciente@clinica.com / paciente123");
        }
    }
}