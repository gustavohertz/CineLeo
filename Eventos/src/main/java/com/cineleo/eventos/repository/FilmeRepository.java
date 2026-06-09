package com.cineleo.eventos.repository;

import com.cineleo.eventos.entity.Filme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilmeRepository extends JpaRepository<Filme, Long> {

    List<Filme> findByStatus(Filme.StatusFilme status);

    boolean existsByNomeIgnoreCase(String nome);
}
