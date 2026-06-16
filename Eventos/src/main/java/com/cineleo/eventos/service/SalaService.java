package com.cineleo.eventos.service;

import com.cineleo.eventos.dto.SalaRequestDTO;
import com.cineleo.eventos.dto.SalaResponseDTO;
import com.cineleo.eventos.entity.Sala;
import com.cineleo.eventos.exception.ConflictException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.cineleo.eventos.repository.SalaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;

    @Transactional(readOnly = true)
    public List<SalaResponseDTO> listarTodas() {
        return salaRepository.findAll().stream()
                .map(SalaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SalaResponseDTO buscarPorId(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com id: " + id));
        return SalaResponseDTO.from(sala);
    }

    @Transactional
    public SalaResponseDTO criar(SalaRequestDTO dto) {
        if (salaRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ConflictException("Já existe uma sala com o nome: " + dto.getNome());
        }
        Sala sala = Sala.builder()
                .nome(dto.getNome())
                .capacidade(dto.getCapacidade())
                .tipo(dto.getTipo())
                .build();
        return SalaResponseDTO.from(salaRepository.save(sala));
    }

    @Transactional
    public SalaResponseDTO atualizar(Long id, SalaRequestDTO dto) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala não encontrada com id: " + id));

        boolean nomeDuplicado = salaRepository.existsByNomeIgnoreCase(dto.getNome())
                && !sala.getNome().equalsIgnoreCase(dto.getNome());
        if (nomeDuplicado) {
            throw new ConflictException("Já existe outra sala com o nome: " + dto.getNome());
        }

        sala.setNome(dto.getNome());
        sala.setCapacidade(dto.getCapacidade());
        sala.setTipo(dto.getTipo());

        return SalaResponseDTO.from(salaRepository.save(sala));
    }

    @Transactional
    public void deletar(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sala não encontrada com id: " + id);
        }
        salaRepository.deleteById(id);
    }
}
