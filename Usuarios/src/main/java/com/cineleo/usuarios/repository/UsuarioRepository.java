package com.cineleo.usuarios.repository;

import com.cineleo.usuarios.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
