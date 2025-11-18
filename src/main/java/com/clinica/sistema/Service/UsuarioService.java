package com.clinica.sistema.Service;

import com.clinica.sistema.Entity.UsuarioEntity;
import com.clinica.sistema.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ UserDetailsService method
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("🔐 BUSCANDO USUARIO: " + email);
        
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("❌ USUARIO NO ENCONTRADO: " + email);
                    return new UsernameNotFoundException("Usuario no encontrado: " + email);
                });

        System.out.println("✅ USUARIO ENCONTRADO: " + usuario.getEmail() + " - Rol: " + usuario.getRol());

        String rolSpring = "ROLE_" + usuario.getRol();
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority(rolSpring)
        );

        System.out.println("🔑 AUTORIDADES: " + authorities);

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                true,    // Siempre activo por ahora
                true,    // accountNonExpired
                true,    // credentialsNonExpired  
                true,    // accountNonLocked
                authorities
        );
    }

    // ✅ MÉTODOS PARA ADMIN CONTROLLER
    public long countAllUsers() {
        return usuarioRepository.count();
    }

    public List<UsuarioEntity> findRecentUsers() {
        // Si tienes el método en el repository, úsalo:
        // return usuarioRepository.findTop5ByOrderByFechaCreacionDesc();
        
        // Si no, usa esta implementación:
        return usuarioRepository.findAll().stream()
                .sorted((u1, u2) -> u2.getFechaCreacion().compareTo(u1.getFechaCreacion()))
                .limit(5)
                .collect(Collectors.toList());
    }

    // ✅ TUS MÉTODOS EXISTENTES
    public List<UsuarioEntity> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<UsuarioEntity> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<UsuarioEntity> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public UsuarioEntity save(UsuarioEntity usuario) {
        if (usuario.getPassword() != null && !usuario.getPassword().startsWith("$2a$")) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }
        return usuarioRepository.save(usuario);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioEntity> findByRol(String rol) {
        return usuarioRepository.findByRol(rol);
    }

    public long count() {
        return usuarioRepository.count();
    }
    
    public long countByRol(String rol) {
        return usuarioRepository.findByRol(rol).size();
    }
    
    public long countByActivo(Boolean activo) {
        return usuarioRepository.findByActivo(activo).size();
    }
    
    public long countByRolAndActivo(String rol, Boolean activo) {
        return usuarioRepository.findByRolAndActivo(rol, activo).size();
    }
    
    public List<UsuarioEntity> findTop5ByOrderByFechaCreacionDesc() {
        return usuarioRepository.findAll().stream()
                .sorted((u1, u2) -> u2.getFechaCreacion().compareTo(u1.getFechaCreacion()))
                .limit(5)
                .collect(Collectors.toList());
    }
    
    public void cambiarPassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }
        
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }
        
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }
}