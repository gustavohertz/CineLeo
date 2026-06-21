package com.cineleo.eventos.service;

import com.cineleo.eventos.dto.EnderecoDTO;
import com.cineleo.eventos.dto.FilmeRequestDTO;
import com.cineleo.eventos.dto.FilmeResponseDTO;
import com.cineleo.eventos.entity.Endereco;
import com.cineleo.eventos.entity.Filme;
import com.cineleo.eventos.exception.ConflictException;
import com.cineleo.eventos.exception.ResourceNotFoundException;
import com.cineleo.eventos.repository.FilmeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class FilmeServiceTest {

    @Mock
    private FilmeRepository filmeRepository;

    @InjectMocks
    private FilmeService filmeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Filme criarFilme() {
        return Filme.builder()
                .id(1L)
                .nome("Matrix")
                .classificacaoIndicativa(Filme.ClassificacaoIndicativa.QUATORZE_ANOS)
                .endereco(Endereco.builder()
                        .logradouro("Rua Teste")
                        .numero("123")
                        .bairro("Centro")
                        .cidade("São Paulo")
                        .uf("SP")
                        .cep("01000-000")
                        .build())
                .status(Filme.StatusFilme.ATIVO)
                .build();
    }

    @Test
    void deveListarTodosOsFilmes() {
        Filme filme = criarFilme();
        when(filmeRepository.findAll()).thenReturn(List.of(filme));

        List<FilmeResponseDTO> resultado = filmeService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("Matrix", resultado.get(0).getNome());
    }

    @Test
    void deveBuscarFilmePorId() {
        Filme filme = criarFilme();
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filme));

        FilmeResponseDTO resultado = filmeService.buscarPorId(1L);

        assertEquals("Matrix", resultado.getNome());
        assertEquals(Filme.StatusFilme.ATIVO, resultado.getStatus());
    }

    @Test
    void deveLancarExcecaoQuandoFilmeNaoEncontrado() {
        when(filmeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> filmeService.buscarPorId(99L));
    }

    @Test
    void deveLancarExcecaoQuandoNomeDuplicado() {
        when(filmeRepository.existsByNomeIgnoreCase("Matrix")).thenReturn(true);

        FilmeRequestDTO dto = new FilmeRequestDTO();
        dto.setNome("Matrix");
        dto.setClassificacaoIndicativa(Filme.ClassificacaoIndicativa.LIVRE);
        dto.setEndereco(EnderecoDTO.builder()
                .logradouro("Rua").numero("1").bairro("B").cidade("C").uf("SP").cep("01000-000").build());

        assertThrows(ConflictException.class, () -> filmeService.criar(dto));
    }

    @Test
    void deveInativarFilme() {
        Filme filme = criarFilme();
        when(filmeRepository.findById(1L)).thenReturn(Optional.of(filme));
        when(filmeRepository.save(any())).thenReturn(filme);

        filmeService.inativar(1L);

        assertEquals(Filme.StatusFilme.INATIVO, filme.getStatus());
        verify(filmeRepository).save(filme);
    }
}
