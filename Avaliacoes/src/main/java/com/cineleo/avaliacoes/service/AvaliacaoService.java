package com.cineleo.avaliacoes.service;

import com.cineleo.avaliacoes.dto.*;
import com.cineleo.avaliacoes.entity.Avaliacao;
import com.cineleo.avaliacoes.enums.StatusAvaliacao;
import com.cineleo.avaliacoes.exception.BusinessException;
import com.cineleo.avaliacoes.exception.ConflictException;
import com.cineleo.avaliacoes.exception.ResourceNotFoundException;
import com.cineleo.avaliacoes.kafka.AvaliacaoProducer;
import com.cineleo.avaliacoes.repository.AvaliacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final AvaliacaoProducer avaliacaoProducer;
    private final CacheService cacheService;

    @Value("${avaliacao.prazo-edicao-horas}")
    private int prazoEdicaoHoras;

    @Value("${avaliacao.max-denuncias-revisao}")
    private int maxDenunciasRevisao;

    public AvaliacaoResponseDTO criar(AvaliacaoRequestDTO dto, String usuarioId, String usuarioNome) {

        if (avaliacaoRepository.existsByUsuarioIdAndFilmeIdAndReservaId(
                usuarioId, dto.getFilmeId(), dto.getReservaId())) {
            throw new ConflictException("Voce ja avaliou este filme com esta reserva.");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .filmeId(dto.getFilmeId())
                .sessaoId(dto.getSessaoId())
                .reservaId(dto.getReservaId())
                .usuarioId(usuarioId)
                .usuarioNome(usuarioNome)
                .nota(dto.getNota())
                .titulo(dto.getTitulo())
                .comentario(dto.getComentario())
                .tags(dto.getTags() != null ? dto.getTags() : List.of())
                .status(StatusAvaliacao.APROVADA)
                .build();

        Avaliacao salva = avaliacaoRepository.save(avaliacao);

        cacheService.invalidarResumo(dto.getFilmeId());

        avaliacaoProducer.publicarAvaliacaoCriada(new AvaliacaoCriadaEvent(
                salva.getId(), salva.getFilmeId(), salva.getUsuarioId(),
                salva.getNota(), salva.getCriadaEm()));

        log.info("[Avaliacao] Criada: id={}, filmeId={}, usuarioId={}", salva.getId(), salva.getFilmeId(), usuarioId);

        return AvaliacaoResponseDTO.from(salva);
    }

    public AvaliacaoListaDTO listarPorFilme(String filmeId) {
        List<Avaliacao> avaliacoes = avaliacaoRepository
                .findByFilmeIdAndStatus(filmeId, StatusAvaliacao.APROVADA);

        double media = avaliacoes.stream()
                .mapToDouble(Avaliacao::getNota)
                .average()
                .orElse(0.0);

        return AvaliacaoListaDTO.builder()
                .filmeId(filmeId)
                .totalAvaliacoes((long) avaliacoes.size())
                .mediaGeral(media)
                .avaliacoes(avaliacoes.stream().map(AvaliacaoResponseDTO::from).toList())
                .build();
    }

    public List<AvaliacaoResponseDTO> listarPorUsuario(String usuarioId) {
        return avaliacaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(AvaliacaoResponseDTO::from)
                .toList();
    }

    public AvaliacaoResumoDTO buscarResumo(String filmeId) {

        AvaliacaoResumoDTO cached = cacheService.buscarResumo(filmeId);
        if (cached != null) {
            log.info("[Cache] Resumo encontrado no cache para filmeId={}", filmeId);
            return cached;
        }

        List<Avaliacao> avaliacoes = avaliacaoRepository
                .findByFilmeIdAndStatus(filmeId, StatusAvaliacao.APROVADA);

        if (avaliacoes.isEmpty()) {
            throw new ResourceNotFoundException("Nenhuma avaliacao encontrada para o filme: " + filmeId);
        }

        double media = avaliacoes.stream().mapToDouble(Avaliacao::getNota).average().orElse(0.0);

        Map<String, Long> distribuicao = new LinkedHashMap<>();
        for (int i = 5; i >= 1; i--) {
            final int nota = i;
            long count = avaliacoes.stream()
                    .filter(a -> a.getNota() >= nota - 0.5 && a.getNota() < nota + 0.5)
                    .count();
            distribuicao.put(String.valueOf(nota), count);
        }

        List<String> topTags = avaliacoes.stream()
                .flatMap(a -> a.getTags().stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();

        AvaliacaoResumoDTO resumo = AvaliacaoResumoDTO.builder()
                .filmeId(filmeId)
                .totalAvaliacoes((long) avaliacoes.size())
                .mediaGeral(Math.round(media * 10.0) / 10.0)
                .distribuicaoNotas(distribuicao)
                .tagsMaisFrequentes(topTags)
                .atualizadoEm(LocalDateTime.now())
                .build();

        cacheService.salvarResumo(filmeId, resumo);

        return resumo;
    }

    public AvaliacaoResponseDTO editar(String id, AvaliacaoRequestDTO dto, String usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Avaliacao nao encontrada ou voce nao e o autor."));

        LocalDateTime prazoLimite = avaliacao.getCriadaEm().plusHours(prazoEdicaoHoras);
        if (LocalDateTime.now().isAfter(prazoLimite)) {
            throw new BusinessException("O prazo de " + prazoEdicaoHoras + "h para edicao expirou.");
        }

        avaliacao.setNota(dto.getNota());
        avaliacao.setTitulo(dto.getTitulo());
        avaliacao.setComentario(dto.getComentario());
        avaliacao.setTags(dto.getTags() != null ? dto.getTags() : List.of());
        avaliacao.setAtualizadaEm(LocalDateTime.now());

        Avaliacao atualizada = avaliacaoRepository.save(avaliacao);
        cacheService.invalidarResumo(avaliacao.getFilmeId());

        return AvaliacaoResponseDTO.from(atualizada);
    }

    public void remover(String id, String usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Avaliacao nao encontrada ou voce nao e o autor."));

        avaliacao.setStatus(StatusAvaliacao.REMOVIDA);
        avaliacao.setAtualizadaEm(LocalDateTime.now());
        avaliacaoRepository.save(avaliacao);

        cacheService.invalidarResumo(avaliacao.getFilmeId());
        avaliacaoProducer.publicarAvaliacaoRemovida(
                new AvaliacaoRemovidaEvent(avaliacao.getId(), avaliacao.getFilmeId(), avaliacao.getUsuarioId()));

        log.info("[Avaliacao] Removida (soft delete): id={}", id);
    }

    public AvaliacaoResponseDTO curtir(String id, String usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada: " + id));

        if (avaliacao.getUsuarioId().equals(usuarioId)) {
            throw new BusinessException("Voce nao pode curtir sua propria avaliacao.");
        }

        if (avaliacao.getCurtidores().contains(usuarioId)) {
            throw new ConflictException("Voce ja curtiu esta avaliacao.");
        }

        avaliacao.getCurtidores().add(usuarioId);
        avaliacao.setCurtidas(avaliacao.getCurtidores().size());

        return AvaliacaoResponseDTO.from(avaliacaoRepository.save(avaliacao));
    }

    public void denunciar(String id, String usuarioId) {
        Avaliacao avaliacao = avaliacaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avaliacao nao encontrada: " + id));

        avaliacao.setDenuncias(avaliacao.getDenuncias() + 1);

        if (avaliacao.getDenuncias() >= maxDenunciasRevisao) {
            avaliacao.setStatus(StatusAvaliacao.PENDENTE);
            log.warn("[Moderacao] Avaliacao {} atingiu {} denuncias e sera revisada.", id, maxDenunciasRevisao);
        }

        avaliacaoRepository.save(avaliacao);
    }

    public void registrarElegibilidade(PagamentoAprovadoEvent evento) {
        log.info("[Elegibilidade] Usuario {} elegivel para avaliar filme {} (reserva: {})",
                evento.getUsuarioId(), evento.getFilmeId(), evento.getReservaId());
    }
}
