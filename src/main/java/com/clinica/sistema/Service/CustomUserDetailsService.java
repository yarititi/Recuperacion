package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("=== 🔍 BUSCANDO USUARIO: " + email + " ===");
        
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ USUARIO NO ENCONTRADO: " + email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });

        System.out.println("✅ USUARIO ENCONTRADO - ID: " + usuario.getId() + ", Rol: " + usuario.getRol());

        String rol = usuario.getRol();
        if (rol == null || rol.trim().isEmpty()) {
            rol = "USER";
        }
        rol = rol.toUpperCase().replace("ROLE_", "");

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .roles(rol)
                .build();
    }
}