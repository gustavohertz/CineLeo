package com.cineleo.recomendacao.service;

import com.cineleo.recomendacao.dto.RecomendacaoDTO;
import com.cineleo.recomendacao.repository.AvaliacaoRecebidaRepository;
import com.cineleo.recomendacao.repository.AvaliacaoRecebidaRepository.FilmeRanking;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class RecomendacaoService {

    private final AvaliacaoRecebidaRepository repository;

    public RecomendacaoService(AvaliacaoRecebidaRepository repository) {
        this.repository = repository;
    }

    public List<RecomendacaoDTO> topFilmes(int limite, long minAvaliacoes) {
        return repository.ranking(minAvaliacoes, PageRequest.of(0, limite))
                .stream().map(this::toDto).toList();
    }

    public List<RecomendacaoDTO> paraUsuario(String usuarioId, int limite, long minAvaliacoes) {
        return repository.rankingParaUsuario(usuarioId, minAvaliacoes, PageRequest.of(0, limite))
                .stream().map(this::toDto).toList();
    }

    private RecomendacaoDTO toDto(FilmeRanking r) {
        Double media = BigDecimal.valueOf(r.getMedia())
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
        return new RecomendacaoDTO(r.getFilmeId(), media, r.getTotal());
    }
}
