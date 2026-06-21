package com.cineleo.eventos.service;

import com.cineleo.eventos.dto.SalaResponseDTO;
import com.cineleo.eventos.entity.Sala;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.cineleo.eventos.repository.SalaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Sala criarSala() {
        return Sala.builder()
                .id(1L)
                .nome("Sala 1")
                .capacidade(100)
                .tipo(Sala.TipoSala.STANDARD)
                .build();
    }

    @Test
    void deveListarTodasAsSalas() {
        Sala sala = criarSala();
        when(salaRepository.findAll()).thenReturn(List.of(sala));

        List<SalaResponseDTO> resultado = salaService.listarTodas();

        assertEquals(1, resultado.size());
        assertEquals("Sala 1", resultado.get(0).getNome());
    }

    @Test
    void deveBuscarSalaPorId() {
        Sala sala = criarSala();
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));

        SalaResponseDTO resultado = salaService.buscarPorId(1L);

        assertEquals("Sala 1", resultado.getNome());
        assertEquals(100, resultado.getCapacidade());
    }

    @Test
    void deveLancarExcecaoQuandoSalaNaoEncontrada() {
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> salaService.buscarPorId(99L));
    }

    @Test
    void deveDeletarSala() {
        when(salaRepository.existsById(1L)).thenReturn(true);

        salaService.deletar(1L);

        verify(salaRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarSalaInexistente() {
        when(salaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> salaService.deletar(99L));
    }
}
