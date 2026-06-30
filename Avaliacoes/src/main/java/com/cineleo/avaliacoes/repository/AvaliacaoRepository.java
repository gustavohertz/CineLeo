package com.cineleo.avaliacoes.repository;

import com.cineleo.avaliacoes.entity.Avaliacao;
import com.cineleo.avaliacoes.enums.StatusAvaliacao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends MongoRepository<Avaliacao, String> {

    List<Avaliacao> findByFilmeIdAndStatus(String filmeId, StatusAvaliacao status);

    List<Avaliacao> findByUsuarioId(String usuarioId);

    boolean existsByUsuarioIdAndFilmeIdAndReservaId(String usuarioId, String filmeId, String reservaId);

    Optional<Avaliacao> findByIdAndUsuarioId(String id, String usuarioId);

    long countByFilmeIdAndStatus(String filmeId, StatusAvaliacao status);
}
