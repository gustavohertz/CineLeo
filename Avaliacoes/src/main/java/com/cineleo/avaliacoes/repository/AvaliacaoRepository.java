package com.cineleo.avaliacoes.repository;

import com.cineleo.avaliacoes.entity.Avaliacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends MongoRepository<Avaliacao, String> {

    List<Avaliacao> findByFilmeId(String filmeId);
    List<Avaliacao> findByUsuarioId(String usuarioId);
    boolean existsByUsuarioIdAndFilmeId(String usuarioId, String filmeId);
    Optional<Avaliacao> findByIdAndUsuarioId(String id, String usuarioId);
}
