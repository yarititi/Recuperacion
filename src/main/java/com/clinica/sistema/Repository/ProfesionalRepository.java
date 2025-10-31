package com.clinica.sistema.Repository;

import com.clinica.sistema.Entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Long> {
    List<Profesional> findByUsuarioRol(String rol);
    Optional<Profesional> findByUsuarioEmail(String email);
}
