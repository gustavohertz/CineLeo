package com.cineleo.eventos.service;

import com.cineleo.eventos.dto.FilmeRequestDTO;
import com.cineleo.eventos.dto.FilmeResponseDTO;
import com.cineleo.eventos.entity.Filme;
import com.cineleo.eventos.exception.ConflictException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.cineleo.eventos.repository.FilmeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmeService {

    private final FilmeRepository filmeRepository;

    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarTodos() {
        return filmeRepository.findAll().stream()
                .map(FilmeResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FilmeResponseDTO> listarAtivos() {
        return filmeRepository.findByStatus(Filme.StatusFilme.ATIVO).stream()
                .map(FilmeResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public FilmeResponseDTO buscarPorId(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado com id: " + id));
        return FilmeResponseDTO.from(filme);
    }

    @Transactional
    public FilmeResponseDTO criar(FilmeRequestDTO dto) {
        if (filmeRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ConflictException("Já existe um filme cadastrado com o nome: " + dto.getNome());
        }
        Filme filme = Filme.builder()
                .nome(dto.getNome())
                .classificacaoIndicativa(dto.getClassificacaoIndicativa())
                .endereco(dto.getEndereco().toEntity())
                .build();
        return FilmeResponseDTO.from(filmeRepository.save(filme));
    }

    @Transactional
    public FilmeResponseDTO atualizar(Long id, FilmeRequestDTO dto) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado com id: " + id));

        boolean nomeDuplicado = filmeRepository.existsByNomeIgnoreCase(dto.getNome())
                && !filme.getNome().equalsIgnoreCase(dto.getNome());
        if (nomeDuplicado) {
            throw new ConflictException("Já existe outro filme cadastrado com o nome: " + dto.getNome());
        }

        filme.setNome(dto.getNome());
        filme.setClassificacaoIndicativa(dto.getClassificacaoIndicativa());
        filme.setEndereco(dto.getEndereco().toEntity());

        return FilmeResponseDTO.from(filmeRepository.save(filme));
    }

    @Transactional
    public void inativar(Long id) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Filme não encontrado com id: " + id));
        filme.setStatus(Filme.StatusFilme.INATIVO);
        filmeRepository.save(filme);
    }
}
