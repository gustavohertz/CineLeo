package com.cineleo.eventos.service;

import com.cineleo.eventos.dto.SessaoRequestDTO;
import com.cineleo.eventos.dto.SessaoResponseDTO;
import com.cineleo.eventos.entity.Filme;
import com.cineleo.eventos.entity.Sala;
import com.cineleo.eventos.entity.Sessao;
import com.cineleo.eventos.exception.BusinessException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.cineleo.eventos.repository.FilmeRepository;
import com.cineleo.eventos.repository.SalaRepository;
import com.cineleo.eventos.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final FilmeRepository filmeRepository;
    private final SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public List<SessaoResponseDTO> listarTodas() {
        return sessaoRepository.findAll().stream()
                .map(SessaoResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SessaoResponseDTO buscarPorId(Long id) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada com id: " + id));
        return SessaoResponseDTO.from(sessao);
    }

    @Transactional(readOnly = true)
    public List<SessaoResponseDTO> listarPorFilme(Long filmeId) {
        return sessaoRepository.findSessoesDisponiveisByFilme(filmeId, LocalDateTime.now()).stream()
                .map(SessaoResponseDTO::from)
                .toList();
    }

    @Transactional
    public SessaoResponseDTO criar(SessaoRequestDTO dto) {
        Filme filme = filmeRepository.findById(dto.getFilmeId())
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado com id: " + dto.getFilmeId()));

        if (filme.getStatus() == Filme.StatusFilme.INATIVO) {
            throw new BusinessException("Não é possível criar sessão para filme inativo");
        }

        Sala sala = salaRepository.findById(dto.getSalaId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com id: " + dto.getSalaId()));

        LocalDateTime inicio = dto.getDataHoraInicio();
        LocalDateTime fim = dto.getDataHoraFim();

        if (inicio.isBefore(LocalDateTime.now())) {
            throw new BusinessException("Data e hora de início não podem ser no passado");
        }
        if (!fim.isAfter(inicio)) {
            throw new BusinessException("Data e hora de fim deve ser posterior ao início");
        }

        List<Sessao> conflitos = sessaoRepository.findConflitosNaSala(sala.getId(), inicio, fim);
        if (!conflitos.isEmpty()) {
            throw new BusinessException("Já existe uma sessão agendada para essa sala nesse horário");
        }

        Sessao sessao = Sessao.builder()
                .filme(filme)
                .sala(sala)
                .dataHoraInicio(inicio)
                .dataHoraFim(fim)
                .preco(dto.getPreco())
                .assentosDisponiveis(sala.getCapacidade())
                .build();

        return SessaoResponseDTO.from(sessaoRepository.save(sessao));
    }

    @Transactional
    public SessaoResponseDTO cancelar(Long id) {
        Sessao sessao = sessaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sessão não encontrada com id: " + id));

        if (sessao.getStatus() == Sessao.StatusSessao.ENCERRADA) {
            throw new BusinessException("Não é possível cancelar uma sessão já encerrada");
        }
        if (sessao.getStatus() == Sessao.StatusSessao.CANCELADA) {
            throw new BusinessException("Sessão já está cancelada");
        }

        sessao.setStatus(Sessao.StatusSessao.CANCELADA);
        return SessaoResponseDTO.from(sessaoRepository.save(sessao));
    }
}
