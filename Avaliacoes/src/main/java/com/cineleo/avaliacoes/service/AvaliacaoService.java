package com.cineleo.avaliacoes.service;

import com.cineleo.avaliacoes.dto.AvaliacaoRequestDTO;
import com.cineleo.avaliacoes.dto.AvaliacaoResponseDTO;
import com.cineleo.avaliacoes.entity.Avaliacao;
import com.cineleo.avaliacoes.exception.BusinessException;
import com.cineleo.avaliacoes.exception.ConflictException;
import com.cineleo.avaliacoes.exception.ResourceNotFoundException;
import com.cineleo.avaliacoes.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoResponseDTO criar(AvaliacaoRequestDTO dto, String usuarioId, String usuarioNome) {

        if (avaliacaoRepository.existsByUsuarioIdAndFilmeId(usuarioId, dto.getFilmeId())) {
            throw new ConflictException("Voce ja avaliou este filme.");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .filmeId(dto.getFilmeId())
                .usuarioId(usuarioId)
                .usuarioNome(usuarioNome)
                .nota(dto.getNota())
                .comentario(dto.getComentario())
                .build();

        Avaliacao salva = avaliacaoRepository.save(avaliacao);

        log.info("[Avaliacao] Criada: id={}, filmeId={}, usuarioId={}", salva.getId(), salva.getFilmeId(), usuarioId);

        return AvaliacaoResponseDTO.from(salva);
    }

    public List<AvaliacaoResponseDTO> listarPorFilme(String filmeId) {
        return avaliacaoRepository.findByFilmeId(filmeId)
                .stream()
                .map(AvaliacaoResponseDTO::from)
                .toList();
    }

    public List<AvaliacaoResponseDTO> minhasAvaliacoes(String usuarioId) {
        return avaliacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(AvaliacaoResponseDTO::from)
                .toList();
    }

    public AvaliacaoResponseDTO editar(String id, AvaliacaoRequestDTO dto, String usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada ou voce nao e o autor."));

        avaliacao.setNota(dto.getNota());
        avaliacao.setComentario(dto.getComentario());
        avaliacao.setAtualizadaEm(LocalDateTime.now());

        return AvaliacaoResponseDTO.from(avaliacaoRepository.save(avaliacao));
    }

    public void remover(String id, String usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada ou voce nao e o autor."));

        avaliacaoRepository.delete(avaliacao);

        log.info("[Avaliacao] Removida: id={}", id);
    }
}
