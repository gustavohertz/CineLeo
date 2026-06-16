package com.cineleo.eventos.repository;

import com.cineleo.eventos.entity.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {

    Optional<Sala> findByNomeIgnoreCase(String nome);

    List<Sala> findByTipo(Sala.TipoSala tipo);

    boolean existsByNomeIgnoreCase(String nome);
}
