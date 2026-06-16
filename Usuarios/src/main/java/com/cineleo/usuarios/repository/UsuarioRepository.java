package com.cineleo.usuarios.repository;

import com.cineleo.usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);

    List<Usuario> findByStatus(Usuario.StatusUsuario status);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
}
